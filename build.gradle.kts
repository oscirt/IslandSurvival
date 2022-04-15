import com.soywiz.korge.gradle.*

buildscript {
	val korgePluginVersion: String by project

	repositories {
		mavenLocal()
		mavenCentral()
		google()
		maven { url = uri("https://plugins.gradle.org/m2/") }
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
		classpath("io.reactivex.rxjava3:rxkotlin:3.0.1")
	}
}

apply<KorgeGradlePlugin>()

korge {
	id = "com.example.example"
	jvmMainClassName = "MainKt"
	orientation = Orientation.LANDSCAPE
// To enable all targets at once

	//targetAll()

// To enable targets based on properties/environment variables
	//targetDefault()

// To selectively enable targets

	targetJvm()
	targetJs()
	targetDesktop()
	targetIos()
	targetAndroidIndirect() // targetAndroidDirect()
}