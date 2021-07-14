package util;

import core.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandMapper {
	private static CommandMapper cm;

	private ConcurrentMap<String, Class<?>> serviceMap;

	public Class<?> findClass(String cmd) {
		return serviceMap.get(cmd);
	}

	private CommandMapper() {
		serviceMap = new ConcurrentHashMap<>();
		try
		{
			Class[] cs = getClasses("core");
			for (Class<?> class1 : cs) {
				
				String s = class1.getName();				
				s = s.split("\\.")[1]; // remove "core." prefix
				if (class1.getSuperclass().equals(Command.class)) { 	
					s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
					if (s.endsWith("Command"))
						s = s.substring(0, s.length() - 7);
					serviceMap.put(s.toLowerCase(), class1);
				}
			}
			System.out.println(serviceMap);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addCommand(String commandName, Class<?> commandClass) {
		serviceMap.put(commandName, commandClass);
	}

	public boolean removeCommand(String commandName) {
		Class c = serviceMap.remove(commandName);
		return c != null;
	}

	public String getAllCommands() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String description = "";
		for (Entry<String, Class<?>> entry : serviceMap.entrySet()) {
			Command c = (Command) (entry.getValue().getDeclaredConstructor().newInstance());
			description += entry.getKey() + "  -v" + c.getVersion() + "\n";
		}
		return description;
	}

	public static CommandMapper getInstance() {

		if (cm == null) {
			cm = new CommandMapper();
		}

		return cm;
	}

	private Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(
						Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}
