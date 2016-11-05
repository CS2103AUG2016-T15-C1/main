//@@author A0139932X
package seedu.task.logic.commands;

import java.io.IOException;

import seedu.task.commons.core.Config;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.commons.util.StringUtil;
import seedu.task.storage.StorageManager;


/**
 * Save storage file to a specific folder.
 */
public class SaveCommand extends Command {

public static final String COMMAND_WORD = "save";

public static final String MESSAGE_USAGE = COMMAND_WORD + ": Change location of the save file "
        + "Parameters: FolderPath"
        + " Example: " + COMMAND_WORD
        + " C:\\Users\\<username>\\Desktop\\CS2103 Tutorial";

    private String changePathLink;
    
    public SaveCommand(String arguments){
        
        this.changePathLink = arguments;
    }
    
    
    @Override
    public CommandResult execute() {
        
        /*
         * Change the folder path and update config.json
         */
        Config config = new Config();
        String configFilePathUsed = Config.DEFAULT_CONFIG_FILE;
        
        try{
            config.setTaskManagerFilePath(changePathLink + "/data/taskmanager.xml");
            ConfigUtil.saveConfig(config, configFilePathUsed);
            
         
            new StorageManager(config.getTaskManagerFilePath(), config.getUserPrefsFilePath());
            model.changeFilePath();
            
            return new CommandResult("Change save path:" + changePathLink + " Updated");
            
        }
        
        catch(IOException e){
			//remove this command from list for undo
			
            return new CommandResult("Failed to save config file: "+ StringUtil.getDetails(e));
        }
        
    }
    
    //@@author

    //@@author A0153411W
	@Override
	public CommandResult executeUndo() {
		return null;
	}

	@Override
	public boolean isReversible() {
		return false;
	}
    //@@author
}
