package net.llvg.exec.preload

import java.io.File
import net.llvg.loliutils.exception.uncheckedCast
import net.minecraft.launchwrapper.ITweaker
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader

@Suppress("UNUSED")
class ExeCTweaker : ITweaker {
        override fun acceptOptions(args: MutableList<String>?, gameDir: File?, assetsDir: File?, profile: String?) {}
        
        override fun injectIntoClassLoader(classLoader: LaunchClassLoader) {
                val tweakClasses: MutableList<String> = Launch.blackboard["TweakClasses"].uncheckedCast()
                tweakClasses.add("cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
        }
        
        override fun getLaunchTarget(): String = throw IllegalStateException("You should not be here!")
        
        override fun getLaunchArguments(): Array<String> = arrayOf()
}