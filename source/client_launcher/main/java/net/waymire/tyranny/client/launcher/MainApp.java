package net.waymire.tyranny.client.launcher;

import java.io.IOException;

import net.waymire.tyranny.client.AppRegistryKeys;
import net.waymire.tyranny.client.ClientEnvironment;
import net.waymire.tyranny.client.configuration.ClientConfig;
import net.waymire.tyranny.client.configuration.ConfigurationFactory;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.dynload.Autoloader;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessagePriority;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.message.StandardMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application 
{
	private Stage primaryStage;
	private Pane rootLayout;
	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	@Override
	public void init()
	{
		ClientEnvironment env = AppRegistry.getInstance().retrieve(ClientEnvironment.class);
		String libraryPath = env.getFullLibraryPath();
		
		LogHelper.info(this, "Loading configuration...");
		ClientConfig config = ConfigurationFactory.getClientConfiguration();
		AppRegistry.getInstance().register(AppRegistryKeys.TYRANNY_CLIENT_CONFIG, config);
		
		LogHelper.info(this, "Auto loading classes...");
		Autoloader.getInstance().load(libraryPath);
		
		LogHelper.info(this, "Announcing That System Is Now Starting.");
		Message starting = new StandardMessage(this, MessageTopics.SYSTEM_STARTING);
		starting.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(starting);
	}
	
	
	@Override
	public void start(Stage primaryStage) 
	{
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Tyranny MMO");
		initRootLayout();
		initMainGui();
		
		LogHelper.info(this, "Announcing That System Has Started.");
		Message started = new StandardMessage(this, MessageTopics.SYSTEM_STARTED);
		started.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(started);
	}

	public Stage getPrimaryStage()
	{
		return primaryStage;
	}
	
	@Override
	public void stop()
	{
		LogHelper.info(this, "Announcing That System Is Now Stopping.");
		Message stopping = new StandardMessage(this, MessageTopics.SYSTEM_STOPPING);
		stopping.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(stopping);
		
		LogHelper.info(this, "Auto unloading classes...");
		Autoloader.getInstance().unload();

		System.exit(0);
	}
		
	private void initRootLayout()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (Pane)loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();			
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	private void initMainGui()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainGUI.fxml"));
			Pane mainGUI = loader.load();
			mainGUI.relocate(0, 28);
			rootLayout.getChildren().add(mainGUI);
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_IDENT_SUCCESS)
	private void onWorldIdentSuccess(Message message)
	{
		Platform.runLater(() -> { primaryStage.close(); });
	}	
}
