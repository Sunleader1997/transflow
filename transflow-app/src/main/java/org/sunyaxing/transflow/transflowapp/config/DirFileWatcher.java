package org.sunyaxing.transflow.transflowapp.config;

import cn.hutool.core.io.FileUtil;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.extensions.TransFlowInput;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 动态加载插件
 */
@Slf4j
@Component
public class DirFileWatcher extends FileAlterationListenerAdaptor implements ApplicationRunner {

    public static String PLUGIN_DIR = "/plugins";
    private final FileAlterationMonitor monitor;
    private static final IOFileFilter FILE_FILTER = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".jar"));

    @Autowired
    private PluginManager pluginManager;
    @Getter
    private HashMap<String, List<TransFlowInput>> pluginInputExtensions;

    public DirFileWatcher() {
        long interval = TimeUnit.SECONDS.toMillis(5);
        FileAlterationObserver observer = new FileAlterationObserver(new File(PLUGIN_DIR), FILE_FILTER);
        observer.addListener(this);
        this.monitor = new FileAlterationMonitor(interval, observer);
        this.pluginInputExtensions = new HashMap<>();
    }

    @Override
    public void onFileDelete(File file) {
        // TODO: 删除插件
        log.info("删除插件 {}", file.getName());
    }

    @Override
    public void onFileChange(File file) {
        // TODO: 重载插件
        log.info("重载插件 {}", file.getName());
    }

    @Override
    public void onFileCreate(File file) {
        try {
            log.info("加载插件 {}", file.getName());
            String pluginId = pluginManager.loadPlugin(file.toPath());
            // 开启插件
            pluginManager.startPlugin(pluginId);
            // 获取拓展
            List<TransFlowInput> transFlowInputs = pluginManager.getExtensions(TransFlowInput.class, pluginId);
            transFlowInputs.forEach(transFlowInput -> {
                transFlowInput.init();
                Thread.ofVirtual().start(transFlowInput);
            });
            pluginInputExtensions.put(pluginId, transFlowInputs);
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileUtil.mkdir(PLUGIN_DIR);
        this.monitor.start();
        loadFromDir();
    }

    public void loadFromDir() {
        FileUtils.listFiles(new File(PLUGIN_DIR), FILE_FILTER, null).forEach(this::onFileCreate);
    }
}
