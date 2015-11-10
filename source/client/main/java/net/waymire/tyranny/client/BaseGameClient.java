package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.JmeSystem;

public abstract class BaseGameClient extends Application {

    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
                                                                         
    protected Node rootNode = new Node("Root Node");
    protected Node guiNode = new Node("Gui Node");
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected FlyByCamera flyCam;
    protected boolean showSettings = true;
    private AppActionListener actionListener = new AppActionListener();
    
    private class AppActionListener implements ActionListener 
    {
    	
        public void onAction(String name, boolean value, float tpf) 
        {
            if (!value) 
            {
                return;
            }

            if (name.equals(INPUT_MAPPING_EXIT)) 
            {
                stop();
            }
        }
    }

    public BaseGameClient() 
    {
    }

    public BaseGameClient( AppState... initialStates ) 
    {
        super();
        
        if (initialStates != null) 
        {
            for (AppState a : initialStates) 
            {
                if (a != null) 
                {
                    stateManager.attach(a);
                }
            }
        }
    }

    @Override
    public void start() 
    {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        if (showSettings) 
        {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }

    public FlyByCamera getFlyByCamera() 
    {
        return flyCam;
    }
    
    /**
     * Retrieves guiNode
     * @return guiNode Node object
     *
     */
    public Node getGuiNode() 
    {
        return guiNode;
    }

    /**
     * Retrieves rootNode
     * @return rootNode Node object
     *
     */
    public Node getRootNode() 
    {
        return rootNode;
    }

    public boolean isShowSettings() 
    {
        return showSettings;
    }

    /**
     * Toggles settings window to display at start-up
     * @param showSettings Sets true/false
     *
     */
    public void setShowSettings(boolean showSettings) 
    {
        this.showSettings = showSettings;
    }

    /**
     *  Creates the font that will be set to the guiFont field
     *  and subsequently set as the font for the stats text.
     */
    protected BitmapFont loadGuiFont() 
    {
        return assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    @Override
    public void initialize() 
    {
        super.initialize();

        // Several things rely on having this
        guiFont = loadGuiFont();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);

        if (inputManager != null) {
        	flyCam = new FlyByCamera(cam);
        	flyCam.setMoveSpeed(1f);
        	flyCam.registerWithInput(inputManager);
        	
            if (context.getType() == Type.Display) {
                inputManager.addMapping(INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
            }

            inputManager.addListener(actionListener, INPUT_MAPPING_EXIT);            
        }

        // call user code
        simpleInitApp();
    }

    @Override
    public void update() 
    {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) 
        {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        // update states
        stateManager.update(tpf);

        // simple update and root node
        simpleUpdate(tpf);
 
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        simpleRender(renderManager);
        stateManager.postRender();        
    }

    public abstract void simpleInitApp();

    public void simpleUpdate(float tpf) 
    {
    }

    public void simpleRender(RenderManager rm) 
    {
    }
}