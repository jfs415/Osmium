package com.kmecpp.osmium.api.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class EventManager {

	//Event key is combination of event class and order 
	private final HashMap<EventKey, HashMap<Object, ArrayList<Method>>> listeners = new HashMap<>();

	//Only for Osmium events
	private final HashMap<Class<? extends EventAbstraction>, TreeSet<RegisteredListener>> events = new HashMap<>();

	public void registerListener(Class<? extends EventAbstraction> eventClass, Order order, Object listenerInstance, Method listener) {
		listener.setAccessible(true);

		if (listener.getParameterTypes().length != 1 || !listener.getParameterTypes()[0].isAssignableFrom(eventClass)) {
			fail(eventClass, listener, "Invalid listener parameters!");
			return;
		} else if (listenerInstance == null) { // || listenerInstance.getClass().isAssignableFrom(listener.getParameterTypes()[0])) {
			fail(eventClass, listener, "Osmium has no listener instance!");
			return;
		}

		TreeSet<RegisteredListener> listeners = events.get(eventClass);
		if (listeners == null) {
			events.put(eventClass, (listeners = new TreeSet<>()));
		}
		listeners.add(new RegisteredListener(listenerInstance, listener, order));
	}

	private static void fail(Class<? extends EventAbstraction> eventClass, Method listener, String message) {
		OsmiumLogger.warn("Failed to register listener for " + eventClass.getName() + "!");
		OsmiumLogger.warn("Listener: " + listener.getClass().getName() + "." + listener);
		OsmiumLogger.warn(message);
	}

	public void callEvent(Event event) {
		TreeSet<RegisteredListener> listeners = events.get(event.getClass());
		if (listeners == null) {
			return;
		}

		//		listeners.sort(Comparator.comparing(RegisteredListener::getOrder));
		for (RegisteredListener listener : listeners) {
			listener.call(event);
		}
	}

	public void registerListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) {
		Class<? extends Event> osmiumEventInterface = eventInfo.getEvent(); //getOsmiumImplementation();
		Class<?>[] nestedClasses = osmiumEventInterface.getClass().getDeclaredClasses();

		//Register event class with children. Ex: void on(BlockEvent)
		boolean registered = false;
		for (Class<?> nestedClass : nestedClasses) {
			if (EventAbstraction.class.isAssignableFrom(nestedClass) && nestedClass.isInterface()) {
				registerSourceListener(plugin, EventInfo.get(Reflection.cast(nestedClass)), order, method, listenerInstance);
				registered = true;
			}
		}

		//Register single event class
		if (!registered) {
			registerSourceListener(plugin, eventInfo, order, method, listenerInstance);
		}
	}

	private void registerSourceListener(OsmiumPlugin plugin, EventInfo eventInfo, Order order, Method method, Object listenerInstance) {
		OsmiumLogger.debug("Registering source listener for " + plugin.getName() + ": "
				+ listenerInstance.getClass().getName() + "." + method.getName()
				+ "(" + eventInfo.getEventName() + ")");
		//		OsmiumLogger.debug(Chat.YELLOW + "    SOURCE CLASSES: " + eventInfo.getSourceClasses());
		for (Class<?> sourceEventClass : eventInfo.getSourceClasses()) {
			EventKey key = new EventKey(sourceEventClass, order);

			boolean firstEventRegistration = !this.listeners.containsKey(key);
			HashMap<Object, ArrayList<Method>> listenerInstances = firstEventRegistration ? new HashMap<>() : this.listeners.get(key);
			if (firstEventRegistration) {
				this.listeners.put(key, listenerInstances);
			}

			ArrayList<Method> listeners = listenerInstances.get(listenerInstance);
			if (listeners == null) {
				listeners = new ArrayList<>();
				listenerInstances.put(listenerInstance, listeners);
			}
			listeners.add(method);

			if (firstEventRegistration) {
				//Register source event once for each sourceEventClass/order combination 
				final Constructor<? extends EventAbstraction> eventWrapperConstructor = getConstructor(eventInfo, sourceEventClass);

				Consumer<Object> globalHandlerForEvent = (sourceEventInstance) -> {
					if (!sourceEventClass.isAssignableFrom(sourceEventInstance.getClass())) {
						return;
					}
					try {
						EventAbstraction event = eventWrapperConstructor.newInstance(sourceEventInstance);
						if (!event.shouldFire()) {
							return;
						}

						for (Entry<Object, ArrayList<Method>> entry : listenerInstances.entrySet()) {
							Object currentListenerInstance = entry.getKey();
							for (Method listenerMethod : entry.getValue()) {
								try {
									//									System.out.println("Invoking: " + listenerMethod + ", " + listenerInstance + ", " + event);
									listenerMethod.invoke(currentListenerInstance, event);
								} catch (Exception ex) {
									//								System.out.println(listenerInstance.getClass().getName());
									OsmiumLogger.warn("An error occurred while firing " + sourceEventClass.getSimpleName() + " for " + currentListenerInstance.getClass().getName());
									ex.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};

				if (Platform.isBukkit()) {
					//					OsmiumLogger.debug(Chat.RED + "REGISTERED LISTENER FOR " + plugin.getName() + ": " + eventInfo.getEventName());
					BukkitAccess.registerListener(plugin, eventInfo, order, method, listenerInstance, globalHandlerForEvent);
				} else if (Platform.isSponge()) {
					SpongeAccess.registerListener(plugin, eventInfo, order, method, listenerInstance, globalHandlerForEvent);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends EventAbstraction> Constructor<T> getConstructor(EventInfo eventInfo, Class<?> sourceEventClass) {
		for (Constructor<?> constructor : eventInfo.getOsmiumImplementation().getConstructors()) {
			if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(sourceEventClass)) {
				return (Constructor<T>) constructor;
			}
		}
		throw new RuntimeException("Failed to extract event wrapper constructor for " + sourceEventClass.getName());
	}

}
