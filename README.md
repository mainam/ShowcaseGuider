# <h1>Android ShowcaseGuider</h1> 
# This library allows create a helper in your application.
# ShowcaseGuider consist of:
#  *Overlay - layer, which will be front on your screen;
#  *ToolTip - prompt, indicating that the user is doing at a given time;
#  *ToolTipPosition - if you don't like, how library setup tooltip on window, you can set tooltip position by yourself.

# <h2>How to setup</h2> 
# <h3>Gradle</h3>
# Add this dependencies in gradle file
```java
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	
dependencies {
		compile 'com.github.DenisMakovskyi:ShowcaseGuider:-27c8befeb7-1'
	}
```
