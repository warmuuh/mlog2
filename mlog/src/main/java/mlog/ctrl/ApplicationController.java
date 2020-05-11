package mlog.ctrl;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import mlog.ctrl.rt.CfgRunContext;
import mlog.ctrl.rt.Channel;
import mlog.ctrl.rt.MessageBuffer;
import mlog.ctrl.rt.MessageQueue;
import mlog.ctrl.rt.StatefulLogParser;
import mlog.domain.Configuration;
import mlog.persistence.PersistenceManager;
import mlog.plugin.LoggerPlugin;
import mlog.plugin.PluginManager;
import mlog.ui.ConnectionView;
import mlog.ui.LogView;
import mlog.ui.MainWindow;
import mlog.ui.StatusView;
import mlog.ui.Toolbar;
import mlog.ui.commands.AppCommand;
import mlog.ui.commands.AppCommand.ClearBuffer;
import mlog.ui.commands.AppCommand.ExecConfiguration;
import mlog.ui.commands.AppCommand.SetAutoscroll;
import mlog.ui.commands.AppCommand.ShowEditConfigurationDialog;
import mlog.ui.commands.AppCommand.StopCurrentConfiguration;
import mlog.ui.dialogs.EditConfigurationDialog;
import reactor.core.Disposable;

@Singleton
@RequiredArgsConstructor
public class ApplicationController {

	private final MainWindow mainWindow;
	private final Toolbar toolbar;
	private final StatusView statusView;
	private final LogView logView;
	private final ConnectionView connectionView;

	private final PluginManager pluginManager;

	private final PersistenceManager persistenceManager;
	private CfgRunContext currentRunContext;


	@PostConstruct
	public void setupApplication(){
		toolbar.OnCommand.add(this::handleCommand);
	}

	public void initApplication() {
		toolbar.setConfigurations(persistenceManager.findAllConfigurations());
	}

	public void destroyApplication(){
		stopConfiguration(currentRunContext);
		persistenceManager.storeAll();
	}

	public void handleCommand(AppCommand command) {

		if (command instanceof ExecConfiguration){
			execConfiguration(((ExecConfiguration) command).getConfiguration());
		} else if (command instanceof StopCurrentConfiguration){
			stopConfiguration(currentRunContext);
		} else if (command instanceof ClearBuffer){
			clearBuffer(currentRunContext);
		} else if (command instanceof ShowEditConfigurationDialog){
			showConfigurationDialog();
		}else if (command instanceof SetAutoscroll){
			logView.setScrollToBottom(((SetAutoscroll) command).isAutoscroll());
		} else {
			throw new IllegalArgumentException("Unsupported command: " + command);
		}

	}

	private void clearBuffer(CfgRunContext currentRunContext) {
		currentRunContext.getBuffer().clearBuffer();
	}

	private void showConfigurationDialog() {
		List<Configuration> allConfigurations = persistenceManager.findAllConfigurations();
		EditConfigurationDialog dialog = new EditConfigurationDialog(allConfigurations, mainWindow);
		dialog.setVisible(true);
		persistenceManager.storeAll();
		toolbar.setConfigurations(allConfigurations);

	}

	private void stopConfiguration(CfgRunContext currentRunContext) {
		statusView.stopMonitor();
		if (currentRunContext != null)
			currentRunContext.stop();
	}

	private void execConfiguration(Configuration config) {
		MessageQueue queue = new MessageQueue();
		MessageBuffer buffer = new MessageBuffer(queue.getMessageBatches());

		List<LoggerPlugin> loggers = pluginManager.loadLoggerPlugins();

		List<Channel> channels = config.getLogger().stream().flatMap(loggerConf ->
				loggers.stream()
						.filter(lp -> lp.getSupportedProtocols().contains(loggerConf.getUri().getScheme()))
						.findAny().stream()
						.flatMap(lp -> lp.createChannels(loggerConf, (channelName) -> new StatefulLogParser(channelName, config.getFormat())).stream())
		).collect(Collectors.toList());


		if (!channels.isEmpty()){
			List<Disposable> disposables = channels.stream().map(c -> c.getMessages().subscribe(queue::process)).collect(Collectors.toList());
			currentRunContext = new CfgRunContext(queue, buffer, channels, disposables);
			statusView.startMonitor(currentRunContext);
			logView.startShowing(currentRunContext.getBuffer(), config.getFormat());
			connectionView.showActiveChannels(channels);
		}
	}

}
