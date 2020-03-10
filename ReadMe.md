# GLata
Glata is a library for creating animations for android using OpenGL ES.

![Alt Text](https://github.com/Yalantis/GLata/blob/master/anim.gif)

## Do I need this?
Okay, most of the time you don't. But sometimes you need to create an animation with lots of objects and/or large images. And sometimes it becomes too heavy for Canvas or MotionLayout. This is where OpenGL comes to help. Treat this library as a lightweight wrapper on OpenGL. You may not only use functionality which this library provides but also use native OpenGL ES calls and tricks. Usually OpenGL initial setup takes a lot of time and it's always nice when someone did it for you, right?

## How to use this library in your project?
Add jitpack repo in your root build.gradle at the end of repositories:
```xml
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
And add dependency in your app module
```xml
	dependencies {
	        implementation 'com.github.Yalantis:GLata:0.1.1'
	}
```

Now you can add `android.opengl.GLSurfaceView` to the xml layout. You can specify any width and height. Also you can use `MotionableGLSurfaceView` if you need your animation to react to gestures.

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.opengl.GLSurfaceView
        android:id="@+id/surfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

Now, you have to extend `Scene` class and override onAttach method.
This is where the initialization of your animation scene happens. Here you create shaders and
objects, set up camera and specify the animations.
```kotlin
    class MyScene() : Scene(sp) {
          override fun onAttach(rp: RendererParams) {
                super.onAttach(rp)
                rendererParams.managers.shaderManager.apply {
			add(Shaders.DEFAULT_COLOR_SHADER)
			add(Shaders.DEFAULT_TEXTURE_SHADER)
		}
                sceneParams.camera.apply {
            		setMaximumVisibleArea(10f, 10f)
            		setPivot(Align.CENTER)
            		setProjectionMatrix()
       		 }
        
                ...
            }

    }
```

You have to create a rectangle, specify its color or texture (you can use R.drawable... or just pass an image name as a string), attach a shader and create animation.
Currently you can choose only movement, scale, rotation or texture switching animation (or all at once and
wrap them into `AnimationList`). Most of the time this is enough but you can extend `IAnimation` and create your own.
```kotlin
        val rect1 = Rectangle.builder()
                .setGradientColor(Color(1f, 0f, 0f, 1f), Color(0f, 1f, 0f, 1f),
                        Color(0f, 0f, 1f, 1f), Color(0.6f, 0.6f, 0.6f, 1f))
                .build()
        rect1.animation = ScalingAnimation(
                startScalingFrom = 0.8f, 
                scaleTo = 1f, 
                scaleTimeMillis = 1000f).apply {
            isInfinite = true
            interpolator = AccelerateDecelerateInterpolator()
        }
        rect1.modelParams.apply {
            setShader(Shaders.DEFAULT_COLOR_SHADER)
            transform.position.x = -1f
        }
        addChild(rect1)
            
        val rect2 = Rectangle
                .builder()
                .setTexture(rendererParams.managers.textureManager.add(Texture(rendererParams, "ava_2")))
                .build()
        rect2.animation = AxisMovementAnimation(
                axis = Axis.Y,
                initialPosition = -1f,
                destinationPosition = 1f,
                travelTimeMillis = 1000f).apply {
                    isInfinite = true
                    returnToInitialAfterFinished = true
                }
        rect2.modelParams.apply {
            setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            transform.position.x = 1f
        }
        addChild(rect2)
```

Then you just need to initialize this surfaceView by calling `initSurface` and pass there context and your animation scene.
```kotlin
    surfaceView.initSurface(context, MyScene())
``` 

That's all for start. For some complex stuff please check out the demo application.


## Let us know!

We’d be really happy if you sent us links to your projects where you use our library. 
Just send an email to github@yalantis.com and do let us know if you have any questions or suggestions.

## License

	The MIT License (MIT)

	Copyright © 2019 Yalantis, https://yalantis.com

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
