package org.sunyaxing.transflow.transflowapp.config;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.sunyaxing.transflow.extensions.TransFlowFilter;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionLifecycle;
import org.sunyaxing.transflow.transflowapp.reactor.TransFlowRunnable;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 动态加载插件
 */
@Component
public class DirFileWatcher extends FileAlterationListenerAdaptor implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DirFileWatcher.class);
    public static String PLUGIN_DIR = "/plugins";
    private final FileAlterationMonitor monitor;
    private static final IOFileFilter FILE_FILTER = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".jar"));

    @Autowired
    private PluginManager pluginManager;
    private TransFlowFilter<String, ?> filters;

    public DirFileWatcher() {
        long interval = TimeUnit.SECONDS.toMillis(5);
        FileAlterationObserver observer = new FileAlterationObserver(new File(PLUGIN_DIR), FILE_FILTER);
        observer.addListener(this);
        this.monitor = new FileAlterationMonitor(interval, observer);
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

        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileUtil.mkdir(PLUGIN_DIR);
        this.monitor.start();
        loadFromDir();
        refresh();
    }

    public void loadFromDir() {
        FileUtils.listFiles(new File(PLUGIN_DIR), FILE_FILTER, null).forEach(this::onFileCreate);
    }

    public void refresh() {
        // 组装过滤器
        List<TransFlowFilter> filters = pluginManager.getExtensions(TransFlowFilter.class);
        TransFlowFilter allFilter = filters.stream().reduce(((filter1, filter2) -> {
            filter1.addNext(filter2);
            return filter1;
        })).orElse(null);
        // 准备输出线程
        List<TransFlowOutput> outputs = pluginManager.getExtensions(TransFlowOutput.class);
        outputs.forEach(ExtensionLifecycle::init);

        // 获取 input 拓展
        List<TransFlowInput> transFlowInputs = pluginManager.getExtensions(TransFlowInput.class);
        transFlowInputs.forEach(ExtensionLifecycle::init);
        // 组装数据流
        transFlowInputs.forEach(transFlowInput -> {
            TransFlowRunnable transFlowRunnable = new TransFlowRunnable(transFlowInput, allFilter, outputs);
            Thread.ofVirtual().start(transFlowRunnable);
        });
    }
}
