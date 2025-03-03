package net.llvg.exec.preload

import java.io.File
import net.llvg.loliutils.exception.uncheckedCast
import net.minecraft.launchwrapper.ITweaker
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import org.apache.logging.log4j.LogManager

@Suppress("UNUSED")
class ExeCTweaker : ITweaker {
        override fun acceptOptions(args: MutableList<String>?, gameDir: File?, assetsDir: File?, profile: String?) {}
        
        override fun injectIntoClassLoader(classLoader: LaunchClassLoader) {
                val tweakClasses: MutableList<String> = Launch.blackboard["TweakClasses"].uncheckedCast()
//                tweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")
                tweakClasses.add("cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
                printClassLoaderExceptions(classLoader)
        }
        
        override fun getLaunchTarget(): String = throw IllegalStateException("You should not be here!")
        
        override fun getLaunchArguments(): Array<String> = arrayOf()
        
        private fun printClassLoaderExceptions(classLoader: LaunchClassLoader) {
                val clazz = LaunchClassLoader::class.java
                val field = clazz.getDeclaredField("classLoaderExceptions")
                field.isAccessible = true
                val value: MutableSet<String> = field.get(classLoader).uncheckedCast()
                
                logger.info("LaunchClassLoader exceptions print begin")
                value.forEach {
                        logger.info(it)
                }
                logger.info("LaunchClassLoader exceptions print end")
        }
}

private val logger = LogManager.getLogger(ExeCTweaker::class.java.simpleName)