package uk.pigpioj;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryUtil {
	static final Logger LOGGER = Logger.getLogger(LibraryUtil.class.getName());

	private static final String OS_NAME = System.getProperty("os.name").replace(" ", "").toLowerCase();
	private static String OS_ARCH = System.getProperty("os.arch").toLowerCase();
	private static final String LINUX_CPUINFO_FILE = "/proc/cpuinfo";
	private static final String ARMV6_CPU_MODEL_NAME = "armv6";
	private static final String ARMV7_CPU_MODEL_NAME = "armv7";

	public static String getCpuArch() {
		String cpu_arch = OS_ARCH;

		Path path = Paths.get(LINUX_CPUINFO_FILE);
		if (path.toFile().exists() && path.toFile().canRead()) {
			try {
				// Determine if this is ARMv6 or ARMv7
				cpu_arch = Files.lines(path).filter(line -> line.startsWith("model name"))
						.map(line -> line.split(":")[1].trim().split("[- ]")[0].trim().toLowerCase())
						.filter(cpu_model -> cpu_model.equals(ARMV6_CPU_MODEL_NAME)
								|| cpu_model.equals(ARMV7_CPU_MODEL_NAME))
						.findFirst().orElse(OS_ARCH);
				// Just in case - fall back to ARMv6
				if (cpu_arch.equals("arm")) {
					cpu_arch = ARMV6_CPU_MODEL_NAME;
				}
			} catch (Throwable t) {
				// Ignore
				LOGGER.log(Level.FINE, "Error processing {} file: {}", new Object[] { LINUX_CPUINFO_FILE, t });
			}
		}

		return cpu_arch;
	}

	public static String getLibExt() {
		// TODO Check if this is comprehensive
		switch (OS_NAME) {
		case "macosx":
			return "dylib";
		case "win":
			return "dll";
		default:
			return "so";
		}
	}

	public static boolean loadLibrary(String libName, Class<?> clz) {
		boolean loaded = false;

		// First try loading from the Java system library path (-Djava.library.path) to
		// allow the bundled libraries to be overridden
		try {
			System.loadLibrary(libName);
			loaded = true;
		} catch (Throwable t) {
			// Ignore
		}

		// If not found, load the appropriate library for this CPU architecture
		if (!loaded) {
			String lib_ext = getLibExt();
			String lib_resource_name = String.format("/lib/%s-%s/lib%s.%s", OS_NAME, getCpuArch(), libName, lib_ext);
			try (InputStream is = clz.getResourceAsStream(lib_resource_name)) {
				if (is == null) {
					LOGGER.log(Level.SEVERE, "Error: unable to find '" + lib_resource_name + "' in the JAR file");
				} else {
					Path path = Files.createTempFile("lib" + libName, lib_ext);
					path.toFile().deleteOnExit();
					Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
					Runtime.getRuntime().load(path.toString());
					loaded = true;
					path.toFile().delete();
				}
			} catch (Throwable t) {
				LOGGER.log(Level.SEVERE, "Error loading library from classpath '" + lib_resource_name + "': " + t);
			}
		}

		return loaded;
	}
}
