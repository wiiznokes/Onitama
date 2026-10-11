Documentation
=========================

This documentation will introduce you to this Game Engine framework for easy and intuitive application development in Java. You will also find help and instructions on how to create your own applications and how to contribute to the framework.

Contents
----------------
##### 1. Engine Presentation
1. Model
	- Game Engine
	- Collision Engine
2. View
	- Game Frame
	- Scene
3. Control
4. Entities
	- Game Object

##### 2. Wafflez: A simple game

##### 3. Creating your game

##### 4. Contributing on the project
2. Contributing on a game 
2. Contributing on the Engine

##### 5. Known problems

--- 
---

Engine Presentation
----------------
As we aimed to apply the _MVC_ design pattern while developing this project this introduction is divided in 3 main parts: Model (Logic), View (Renderer), Controller (Input manager). A fourth part will introduce you to Game Objects and base classes provided, which are the base of the application development process using this framework. 

#### 1. Model

The _Model_ is composed as for now by the Game Engine alone. Plans for the future versions is to include also a Collision Engine.

##### Settings

	Engine/Global/Settings.java

This simple class holds static values used by the scene and that are available for the developed application.

| Name | Function |
|---|---|
| Dimension resolution | Defines the application window size. |
| boolean fullscreen | Whether the application is currently on fullscreen or not. |
| boolean resizable | Whether the window can be resized or not. |
| boolean stretch | Whether on resizing the window the contents should be resized. |
| int fixfps | Desired application FPS. |
| String applicationName | The name of the application. |
| int fullscreen_key | Key to be pressed to toggle fullscreen. |
| int undo_key | Key to be pressed to undo an action. |
| int redo_key | Key to be pressed to redo an action. |

> This method is temporary and will probably be replaced in the future by a JSON.

---

##### Game Engine

	Engine/Core/Engines/GameEngine.java

The Game Engine runs on a independent thread and encapsulates the main loop of the application offering a friendly interface for the core of your program. \
It is responsible for managing the application window, updating components and refreshing the interface regularly using a frequency of your choosing (default 60 FPS). \
The updates, however, are updated on a frequency defined by the engine itself and its set to 60 UPS (updated per second). This is the frequency in which objects on the game are updated and its important to keep a fixed frequency across all devices. \
Here is quick explanation of the difference between FPS and UPS:
1. **FPS** (Frames Per Second):
	- FPS refers to the number of frames (images) displayed per second on your screen.
2. **UPS** (Updates Per Second):
	- UPS, on the other hand, represents the frequency at which the game's logic or simulation is updated per second.
	- It includes calculations related to player actions, AI behavior, physics simulations, and any other game mechanics.
	- Having a high UPS ensures that the game responds quickly to player inputs and maintains consistent gameplay mechanics.
	- In multiplayer games, UPS becomes even more critical to ensure synchronized gameplay among all players and prevent issues like desync or lag.

Here is a brief overview of the interface implemented:

| CONSTRUCTOR  | DESCRIPTION |
|---|---|
| public GameEngine() | Constructs a new GameEngine instance. |

| METHOD  | DESCRIPTION |
|---|---|
| public void start() | Starts the game engine and the game loop. To be run after properly configuring the Engine |
| public void setMainScene(Scene scn) | Sets the main scene of the game. |
| public void setCurrentScene(Scene scn) | Sets the current active scene of the game. |
| public Scene getCurrentScene() | Gets the current active scene of the game. |
| public void setResolution(Dimension res) | Sets the resolution of the game window. |
| public void stop() | Stops the game engine and exits the program. |
| public void pause() | Pauses the game loop. |
| public int getFPS() | return number of frames per second |
| public int getUPS() | return number of updates per second |
| public void setIcon(Image appicon) | Sets the icon for the application window. |

---

#### 2. View

The view is based on _Java Swing_ and is divided in 2 parts:
1. **Game Frame** : The application window (_JFrame_)
2. **Scene** : Window content (_JPanel_)

In short the Game Frame is unique and managed by the Game Engine and should not be modified directly by the developed application. The Scene, on the other hand, is designed to be a base to be used during application development that will hold all game entities in your project, it is a dynamic and can be replaced during runtime.

##### Game Frame

	Engine/Core/Renderer/GameFrame.java

As it is managed by the Game Engine, the Game Frame itself does not provide any interface but can be configured trough the Game Engine interface.
It handles the application window and content rendering.

---

##### Scene

	Engine/Core/Renderer/Scene.java

This is the base of any game. All application components should be added and rely on the Scene to be handled by the Game Engine, it will provide a visual base for content and assure proper rendering and updating.
We will talk more about components on section 4 (Entities), which are fundamental building blocks used to construct scenes in your game.
A game scene is essentially a self-contained unit that represents a portion of your game world or a specific part of your game. It encapsulates everything needed for that particular section, including objects, characters, scripts, visuals, and logic.

>- You can create complex scenes composed of smaller, reusable components.
>- Each component within a scene can have its own properties, scripts, and behaviors.
>- Scenes can contain logic for gameplay mechanics, character movements, AI behavior, user interfaces, and more.
>- Scenes promote reusability and modularity in game development.
>- These scenes can be easily reused across multiple parts of your game or even in different projects.
>- Scenes can be dynamically loaded and unloaded during runtime.

Here is a brief overview of its interface:

| CONSTRUCTOR | DESCRIPTION |
|---|---|
| public Scene() | Constructs a new Scene instance. Initializes the scene composition and sets up the controller for input events. |
| public Scene(Dimension resolution) | Constructs a new Scene instance having the given dimensions. Initializes the scene composition and sets up the controller for input events.

| ATTRIBUTES | DESCRIPTION |
|---|---|
| Controller control | Controller for handling input events. |
| public LinkedList\<GameObject\> components | List of game objects in the scene. |

| METHOD | DESCRIPTION |
|---|---|
| public void addComponent(GameObject comp) | Adds a game object to the scene. |
| public void addComponent(int index, GameObject comp) | Adds a game object to the scene at the specified index. |
| public void removeComponent(int index) | Removes the game object at the specified index from the scene. |
| public void removeComponent(GameObject comp) | Removes the specified game object from the scene. |
| public void refresh() | Refreshes the scene by repainting it. |

---

#### 3. Control

	Engine/Core/Controller/Controller.java

The controller intercepts mouse and keyboard input events and redirects them to the concerned components on the current application Scene. It can also manage default Engine behaviours for certain predefined keys.
There is no interface provided for the user and its essentially an independent component of the framework.

Input events are redirected to Game Objects by calling their respective _input()_ method, forwarding the event from the scene to its concerned component.

---

#### 4. Entities

An entity or game object is a fundamental building block used to construct scenes in your game. Entities represent various elements such as objects, characters, sprites, physics bodies (for future versions), user interface elements, logic elements, and more.

All entities are based on an abstract class GameObject that implement basic methods to manage the component  in the engine.

##### GameObject

	Engine/Entities/GameObject.java

The GameObject class serves as a foundational template for creating objects within the game world, parent to most other entities implemented in the project.

The **process()** and **input(...)** methods play crucial roles in managing the object's behavior and response to game events. Here's an explanation of each:

- **process()**:

	> public void process(double delta) {\
    > // To be implemented in subclasses\
    > }

	- Is intended to be overridden in subclasses and is called 60 times per second (optimally) to update the object's state based on elapsed time (delta).
	- The delta parameter represents the time elapsed since the last update in seconds. It allows for smooth and consistent updates regardless of FPS and UPS rates.
	- Inside the process method, you typically handle game logic related to movement, animation, physics, AI behavior, or any other continuous processes that need to be updated over time.
	- By utilizing delta, you ensure that your game behaves consistently across different hardware and frame rates, as the updates are scaled based on the elapsed time.

- **input(...)**:

    >public void input(KeyEvent e) {\
    >    // To be implemented in subclasses\
    >}
	>
    >public void input(MouseEvent e) {\
    >    // To be implemented in subclasses\
    >}	
  
    - The input(KeyEvent e) method is called when a keyboard event occurs, such as a key press or release.
    - The input(MouseEvent e) method is called when a mouse event occurs, such as a mouse click.
    - These methods allow your GameObject instances to respond to user input, enabling interactions with the game world, player controls, and user interface elements.
    - You can override these methods in subclasses to define custom input handling logic tailored to the specific behavior of each object.
    
    - > For example, a player character might respond to keyboard input for movement, while a clickable button might respond to mouse clicks.
    
---

Using the Engine
----------------

To start using the Engine, you need to create a scene where your game objects will be displayed.

    
    import Engine.Core.Renderer.Scene;
    
    // Create a new scene
    Scene scene = new Scene();
        

Adding Objects to the Scene
---------------------------

You can add game objects, such as sprites, UI elements, or custom entities, to the scene.

    
    import Engine.Entities.GameObject;
    import Engine.Entities.UI.ColorArea;
    
    // Create a game object
    GameObject gameObject = new GameObject();
    
    // Add the game object to the scene
    scene.addComponent(gameObject);
    
    // Create a colored area UI object
    ColorArea colorArea = new ColorArea(Color.RED, new Dimension(100, 100));
    
    // Add the color area to the scene
    scene.addComponent(colorArea);
        

Setting Up the Game Engine
--------------------------

Before running your game, you need to set up the game engine.

    
    import Engine.Core.Engines.GameEngine;
    
    // Create a new game engine instance
    GameEngine engine = new GameEngine();
    
    // Set the current scene
    engine.setCurrentScene(scene);
        

Starting the Engine
-------------------

Once everything is set up, you can start the game engine to run your game.

    
    // Start the game engine
    engine.start();
        

Handling Input
--------------

You can handle user input, such as keyboard or mouse events, to interact with your game objects.
You just need to ovewrite those functions in any class inherited from a GameObject.

    
    import java.awt.event.KeyEvent;
    import java.awt.event.MouseEvent;
    
    // Implement input handling methods in your game objects
    public void input(KeyEvent e) {
        // Handle keyboard input
    }
    
    public void input(MouseEvent e) {
        // Handle mouse input
    }


Updating Game Logic
-------------------

Each GameObject in your scene can have a `process()` method, which is called at every frame update. You can use this method to update the game logic, perform calculations, and manage object behaviors.

    
    // Example implementation of the process method in a game object
    public void process(double delta) {
        // Update game logic here
        // Perform calculations, handle collisions, etc.
    }
        

Conclusion
----------

This documentation covers the basic usage of the Engine framework, including scene creation, object addition, engine setup, input handling, and game logic updating. You can explore additional features and functionalities provided by the Engine to create more complex and interactive applications. Refer to Rafael for more detailed information and advanced usage.

New UI features have been added, such as support for menus using the default Java Swing library, documentation will be added soon. Meanwhile you can also check the provided code for the WaffleGame menus implementation behaviors.