package org.example.plugin;

import org.hotswap.agent.annotation.Init;
import org.hotswap.agent.annotation.OnClassFileEvent;
import org.hotswap.agent.annotation.OnClassLoadEvent;
import org.hotswap.agent.annotation.Plugin;
import org.hotswap.agent.command.Scheduler;
import org.hotswap.agent.javassist.CannotCompileException;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.javassist.NotFoundException;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.PluginManagerInvoker;

import static org.hotswap.agent.annotation.FileEvent.CREATE;
import static org.hotswap.agent.annotation.FileEvent.MODIFY;

/**
 * The plugin system annotation is similar to Spring MVC way - use method annotation with variable
 * method attributes types. See each annotation javadoc for available attribute types and usage.
 * <p/>
 * Always be aware of which classloader your code use (Application or agent classloader?) More on
 * classloader issues in
 * <a href="https://github.com/HotswapProjects/HotswapAgent/blob/master/HotswapAgent/README.md">Agent documentation</a>
 */
@Plugin(name = "ExamplePlugin", description = "Hotswap agent plugin as part of normal application.",
        testedVersions = "Describe dependent framework version you have tested the plugin with.",
        expectedVersions = "Describe dependent framework version you expect to work the plugin with.")
public class ExamplePlugin {
    public static final String PLUGIN_PACKAGE = "org.example.plugin";

    // as an example, we will enhance this service to return content of examplePlugin.resource
    // and class load/reload counts in agentexamples's helloWorld service method
    public static final String MAIN_PANEL = "org.example.gui.panel.MainPanel";

    // Agent logger is a very simple custom logging mechanism. Do not use any common logging framework
    // to avoid compatibility and classloading issues.
    private static AgentLogger LOGGER = AgentLogger.getLogger(ExamplePlugin.class);

    /**
     * Any plugin has to have at least one static @Transform method to hook initialization code. It is usually
     * some key framework method. Call PluginManager.initializePlugin() to create new plugin instance and
     * initialize agentexamples with the application classloader. Than call one or more methods on the plugin
     * to pass reference to framework/application objects.
     *
     * @param ctClass see @Transform javadoc for available parameter types. CtClass is convenient way
     *                to enhance method bytecode using javaasist
     */
    @OnClassLoadEvent(classNameRegexp = MAIN_PANEL)
    public static void transformTestEntityService(CtClass ctClass) throws NotFoundException, CannotCompileException {

        // You need always find a place from which to initialize the plugin.
        // Initialization will create new plugin instance (notice that transformTestEntityService is
        // a static method), inject agent services (@Inject) and register event listeners (@Transform and @Watch).
        String src = PluginManagerInvoker.buildInitializePlugin(ExamplePlugin.class);

        // If you need to call a plugin method from application context, there are some issues
        // Always think about two different classloaders - application and agent/plugin. The parameter
        // here cannot be of type TestEntityService because the plugin does not know this type at runtime
        // (although agentexamples will compile here!). If you call plugin method, usually only basic java types (java.lang.*)
        // are safe.
        src += PluginManagerInvoker.buildCallPluginMethod(ExamplePlugin.class, "registerService", "this", "java.lang.Object");

        // do enhance default constructor using javaasist. Plugin manager (TransformHandler) will use enhanced class
        // to replace actual bytecode.
        ctClass.getDeclaredConstructor(new CtClass[0]).insertAfter(src);

        LOGGER.debug(MAIN_PANEL + " has been enhanced.");
    }

    /**
     * All compiled code in ExamplePlugin is executed in agent classloader and cannot access
     * framework/application classes. If you need to call a method on framework class, use application
     * classloader. It is injected on plugin initialization.
     */
    @Init
    ClassLoader appClassLoader;

    /**
     * Called from HelloWorldService enhanced constructor. Note that the service cannot be typed to
     * HelloWorldService class - the class is not known to agent classloader (agentexamples lives only in the
     * application classloader).
     */
    public void registerService(Object mainPanel) {
        this.mainPanel = mainPanel;
        LOGGER.info("Plugin {} initialized on service {}", getClass(), this.mainPanel);
    }

    // the service, please note that agentexamples cannot be typed here
    Object mainPanel;


    // Scheduler service - use to run a command asynchronously and merge multiple similar commands to one execution
    // static  - Scheduler and other agent services are available even in static context (before the plugin is initialized)
    @Init
    Scheduler scheduler;



    @OnClassFileEvent(classNameRegexp = MAIN_PANEL, events = {CREATE, MODIFY})
    public void changeClassFile(String className) {
        scheduler.scheduleCommand(new ReloadClassCommand(appClassLoader, className, mainPanel), 2000);
    }


}