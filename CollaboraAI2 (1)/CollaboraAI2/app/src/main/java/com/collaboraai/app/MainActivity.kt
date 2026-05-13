Run cd "./CollaboraAI2 (1)/CollaboraAI2"

Welcome to Gradle 8.7!

Here are the highlights of this release:
 - Compiling and testing with Java 22
 - Cacheable Groovy script compilation
 - New methods in lazy collection properties

For more details see https://docs.gradle.org/8.7/release-notes.html

To honour the JVM settings for this build a single-use Daemon process will be forked. For more on this, please refer to https://docs.gradle.org/8.7/userguide/gradle_daemon.html#sec:disabling_the_daemon in the Gradle documentation.
Daemon will be stopped at the end of the build 
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors SKIPPED
> Task :app:dataBindingMergeDependencyArtifactsDebug
> Task :app:generateDebugResValues
> Task :app:generateDebugResources
> Task :app:packageDebugResources
> Task :app:parseDebugLocalResources
> Task :app:mapDebugSourceSetPaths
> Task :app:checkDebugAarMetadata
> Task :app:createDebugCompatibleScreenManifests
> Task :app:extractDeepLinksDebug
> Task :app:mergeDebugResources
> Task :app:processDebugMainManifest
> Task :app:dataBindingGenBaseClassesDebug
> Task :app:processDebugManifest
> Task :app:javaPreCompileDebug
> Task :app:mergeDebugShaders
> Task :app:compileDebugShaders NO-SOURCE
> Task :app:generateDebugAssets UP-TO-DATE
> Task :app:mergeDebugAssets
> Task :app:compressDebugAssets
> Task :app:desugarDebugFileDependencies
> Task :app:processDebugManifestForPackage
> Task :app:checkDebugDuplicateClasses
> Task :app:mergeDebugStartupProfile
> Task :app:processDebugResources
> Task :app:mergeExtDexDebug
> Task :app:mergeDebugJniLibFolders
> Task :app:mergeLibDexDebug
> Task :app:mergeDebugNativeLibs NO-SOURCE
> Task :app:stripDebugDebugSymbols NO-SOURCE
> Task :app:validateSigningDebug
> Task :app:writeDebugAppMetadata
> Task :app:writeDebugSigningConfigVersions
e: file:///home/runner/work/COLLABORAI-V2.0/COLLABORAI-V2.0/CollaboraAI2%20(1)/CollaboraAI2/app/src/main/java/com/collaboraai/app/MainActivity.kt:34:5 'onNewIntent' overrides nothing.

e: file:///home/runner/work/COLLABORAI-V2.0/COLLABORAI-V2.0/CollaboraAI2%20(1)/CollaboraAI2/app/src/main/java/com/collaboraai/app/MainActivity.kt:35:27 Argument type mismatch: actual type is 'android.content.Intent?', but 'android.content.Intent' was expected.
> Task :app:compileDebugKotlin FAILED

FAILURE: Build failed with an exception.
29 actionable tasks: 29 executed

* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 1m 37s
Error: Process completed with exit code 1.
