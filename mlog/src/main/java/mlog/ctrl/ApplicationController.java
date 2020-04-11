package mlog.ctrl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import mlog.ctrl.rt.CfgRunContext;
import mlog.ctrl.rt.MessageBuffer;
import mlog.ctrl.rt.MessageQueue;
import mlog.domain.Configuration;
import mlog.persistence.PersistenceManager;
import mlog.plugin.LoggerPlugin;
import mlog.plugin.PluginManager;
import mlog.ui.ConnectionView;
import mlog.ui.LogView;
import mlog.ui.StatusView;
import mlog.ui.Toolbar;
import mlog.ui.commands.AppCommand;
import mlog.ui.commands.AppCommand.ExecConfiguration;
import mlog.ui.commands.AppCommand.StopCurrentConfiguration;

@Singleton
@RequiredArgsConstructor
public class ApplicationController {

	private final Toolbar toolbar;
	private final StatusView statusView;
	private final LogView logView;
	private final ConnectionView connectionView;

	private final PluginManager pluginManager;

	private final PersistenceManager persistenceManager;
	private CfgRunContext currentRunContext;


	@PostConstruct
	public void initApplication() {
		toolbar.setConfigurations(persistenceManager.findAllConfigurations());
		toolbar.OnCommand.add(this::handleCommand);
	}


	public void handleCommand(AppCommand command) {

		if (command instanceof ExecConfiguration){
			execConfiguration(((ExecConfiguration) command).getConfiguration());
		} else if (command instanceof StopCurrentConfiguration){
			stopConfiguration(currentRunContext);
		} else {
			throw new IllegalArgumentException("Unsupported command: " + command);
		}

	}

	private void stopConfiguration(CfgRunContext currentRunContext) {
		statusView.stopMonitor();
		currentRunContext.getQueue().stop();
	}

	private void execConfiguration(Configuration config) {
		MessageQueue queue = new MessageQueue();
		MessageBuffer buffer = new MessageBuffer(queue.getMessageBatches());

		List<LoggerPlugin> loggers = pluginManager.loadLoggerPlugins();
		loggers.stream()
				.filter(lp -> lp.getSupportedProtocols().contains(config.getLogger().getUri().getScheme()))
				.findAny()
				.map(lp -> lp.createChannels(config.getLogger()))
				.ifPresent(channels -> {
					channels.forEach(c -> c.getMessages().subscribe(queue::process));
					currentRunContext = new CfgRunContext(queue, buffer, channels);
					statusView.startMonitor(currentRunContext);
					logView.startShowing(currentRunContext.getBuffer(), config.getFormat());
					connectionView.showActiveChannels(channels);
				});
	}

}
