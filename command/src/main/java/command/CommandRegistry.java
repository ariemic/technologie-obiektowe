package command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CommandRegistry {

	private ObservableList<Command> commandStack = FXCollections
			.observableArrayList();
	private ObservableList<Command> redoStack = FXCollections.observableArrayList();

	public void executeCommand(Command command) {
		command.execute();
		commandStack.add(command);
		// musimy wyczyścić redoStack, ponieważ po wykonaniu nowej operacji zmienia się stan stosu,
		// jest to stan dla poprzedniej operacji
		redoStack.clear();
	}

	public void redo() {
		if (!redoStack.isEmpty()){
			Command lastCommand = redoStack.getLast();
			lastCommand.redo();
			commandStack.add(lastCommand);
		}
		
	}

	public void undo() {
		if (!commandStack.isEmpty()){
			Command lastCommand = commandStack.getLast();
			lastCommand.undo();
			redoStack.add(lastCommand);
		}
	}

	public ObservableList<Command> getCommandStack() {
		return commandStack;
	}
	public ObservableList<Command> getRedoStack() {
		return redoStack;

	}
}
