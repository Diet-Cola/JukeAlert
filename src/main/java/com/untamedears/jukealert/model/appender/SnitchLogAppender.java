package com.untamedears.jukealert.model.appender;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.database.JukeAlertDAO;
import com.untamedears.jukealert.events.LoggableActionEvent;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.ActionCacheState;
import com.untamedears.jukealert.model.actions.LoggedActionFactory;
import com.untamedears.jukealert.model.actions.abstr.SnitchAction;
import com.untamedears.jukealert.model.appender.annotations.AppenderEventHandler;
import com.untamedears.jukealert.model.appender.config.LimitedActionTriggerConfig;
import com.untamedears.jukealert.util.JukeAlertPermissionHandler;

public class SnitchLogAppender extends ConfigurableSnitchAppender<LimitedActionTriggerConfig> {

	public static final String ID = "log";

	private List<SnitchAction> actions;
	private boolean hasLoadedAll;

	public SnitchLogAppender(Snitch snitch, ConfigurationSection config) {
		super(snitch, config);
		this.actions = new LinkedList<>();
		this.hasLoadedAll = false;
		if (snitch.getId() != -1) {
			loadLogs();
		}
		else {
			hasLoadedAll = true;
			actions = new LinkedList<>();
		}
	}

	@AppenderEventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void acceptAction(LoggableActionEvent event) {
		SnitchAction log = event.getAction();
		if (!config.isTrigger(log.getIdentifier())) {
			return;
		}
		if (snitch.hasPermission(log.getPlayer(), JukeAlertPermissionHandler.getSnitchImmune())) {
			return;
		}
		actions.add(log);
		if (snitch.getId() != -1) {
			int id = JukeAlert.getInstance().getLoggedActionFactory().getInternalID(log.getIdentifier());
			if (id != -1) {
				JukeAlert.getInstance().getDAO().insertLogAsync(id, getSnitch(), log);
			}
		}
		else {
			snitch.setDirty();
		}
	}

	@Override
	public void persist() {
		JukeAlertDAO dao = JukeAlert.getInstance().getDAO();
		LoggedActionFactory fac = JukeAlert.getInstance().getLoggedActionFactory();
		Iterator<SnitchAction> iter = actions.iterator();
		while (iter.hasNext()) {
			SnitchAction action = iter.next();
			switch (action.getCacheState()) {
			case NEW:
				int id = fac.getInternalID(((SnitchAction)action).getIdentifier());
				if (id != -1) {
					dao.insertLog(id, getSnitch(), action.getPersistence());
					action.setCacheState(ActionCacheState.NORMAL);
				}
				continue;
			case DELETED:
				dao.deleteLog(action);
				iter.remove();
				continue;
			case NORMAL:
				continue;
			}
		}
	}

	private void loadLogs() {
		synchronized (actions) {
			try {
				actions.addAll(JukeAlert.getInstance().getDAO().loadLogs(getSnitch()));
				hasLoadedAll = true;
			} finally {
				actions.notifyAll();
			}
		}
	}
	
	public void deleteLogs() {
		//TODO
	}

	public List<SnitchAction> getFullLogs() {
		if (!hasLoadedAll) {
			synchronized (actions) {
				while (!hasLoadedAll) {
					try {
						actions.wait();
					} catch (InterruptedException e) {
						// welp
					}
				}
			}
		}
		return actions;
	}

	@Override
	public boolean runWhenSnitchInactive() {
		return false;
	}

	@Override
	public Class<LimitedActionTriggerConfig> getConfigClass() {
		return LimitedActionTriggerConfig.class;
	}

}
