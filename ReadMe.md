# GLata
GLata is an Android library for creating multi-object and resource-heavy animations using OpenGL for Embedded Systems.

![Alt Text](https://github.com/Yalantis/GLata/blob/master/anim.gif)

## Why do I need GLata?
I bet you’re familiar with Canvas, ObjectAnimator, and MotionLayout. These standard Android solutions are undeniably great for creating simple animations. However, they may not always be enough for drawing massive animations with loads of moving objects and/or large images. This may be a task for OpenGL ES.
However, setting up OpenGL usually takes a lot of time, especially if you’ve never done it before. GLata not only does the initial setup for you but suggests tools to start drawing complex animations right away. These include tools for:
- Creating a rectangle and applying an image to it
- Setting up a camera and specifying the visible area 
- Applying a bunch of shaders including blur and tint
- Applying basic animations: movement, scaling, rotation, texture switching
- Using the ETC1 compressed texture format for faster loading

And one more thing: GLata lets you use native OpenGL ES calls and tricks, which is not usually the case for large graphics frameworks. Consider this library a lightweight wrapper for OpenGL ES and your launching pad for creating complex animations.

## How can I use GLata in my project?
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
wrap them into `AnimationList`). Most of the time this is enough but you can implement an `IAnimation` interface and create your own.
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
