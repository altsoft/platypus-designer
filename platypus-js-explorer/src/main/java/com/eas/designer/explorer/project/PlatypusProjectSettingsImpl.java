package com.eas.designer.explorer.project;

import com.eas.designer.application.project.ClientType;
import com.eas.designer.application.project.AppServerType;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.util.StringUtils;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.EditableProperties;

/**
 * The facade class for the settings of a project.
 *
 * @author vv
 */
public class PlatypusProjectSettingsImpl implements PlatypusProjectSettings {

    public static final int DEFAULT_PLATYPUS_SERVER_PORT         = 7500;
    public static final int DEFAULT_SERVLET_CONTAINER_PORT       = 8085;
    public static final int DEFAULT_PLATYPUS_CLIENT_DEBUG_PORT   = 5001;
    public static final int DEFAULT_PLATYPUS_SERVER_DEBUG_PORT   = 5004;
    public static final int DEFAULT_SERVLET_CONTAINER_DEBUG_PORT = 5006;
    public static final Level DEFAULT_LOG_LEVEL                  = Level.INFO;
    public static final String DEFAULT_APP_FOLDER                = "app"; //NOI18N
    public static final String PROJECT_COMMANDS_FILE             = ".platypus"; //NOI18N
    public static final String PROJECT_PRIVATE_SETTINGS_FILE     = "private.properties"; //NOI18N
    public static final String PROJECT_SETTINGS_FILE             = "project.properties"; //NOI18N
    public static final String PROJECT_DISPLAY_NAME_KEY          = "project.displayName"; //NOI18N
    public static final String CLEAN_COMMAND_KEY                 = "project.cleanCommand"; //NOI18N
    public static final String BUILD_COMMAND_KEY                 = "project.buildCommand"; //NOI18N
    public static final String ACCEPT_DESIGNER_DATASOURCES_KEY   = "project.acceptNetBeansDatasources"; //NOI18N
    public static final String DEFAULT_DATA_SOURCE_ELEMENT_KEY   = "project.generalDataSource"; //NOI18N
    public static final String RUN_ELEMENT_KEY                   = "run.module"; //NOI18N
    public static final String GLOBAL_API_KEY                    = "run.globalAPI";//NOI18N
    public static final String CLIENT_TYPE_KEY                   = "run.clientType"; //NOI18N
    public static final String SERVER_TYPE_KEY                   = "run.serverType"; //NOI18N
    public static final String SOURCE_PATH_KEY                   = "run.sourcePath"; //NOI18N
    public static final String RUN_USER_NAME_KEY                 = "user.name"; //NOI18N
    public static final String RUN_USER_PASSWORD_KEY             = "user.password"; //NOI18N
    public static final String SERVLET_CONTAINER_PORT_KEY        = "http.port";//NOI18N
    public static final String PLATYPUS_SERVER_PORT_KEY          = "platypus.port";//NOI18N
    public static final String WEB_APPLICATION_CONTEXT_KEY       = "webApplication.context";//NOI18N
    public static final String AUTO_APPLY_WEB_XML_KEY            = "webApplication.autoApplyWebXml"; //NOI18N
    public static final String ENABLE_SECURITY_REALM_KEY         = "webApplication.enableSecurityRealm";//NOI18N
    public static final String BROWSER_CUSTOM_URL_KEY            = "browser.customUrl";//NOI18N
    public static final String BROWSER_CACHE_BUSTING_KEY         = "browser.cacheBusting";//NOI18N
    public static final String BROWSER_RUN_COMMAND_KEY           = "browser.runCommand"; //NOI18N
    public static final String PLATYPUS_CLIENT_URL_KEY           = "platypusClient.customUrl";//NOI18N
    public static final String PLATYPUS_CLIENT_OPTIONS_KEY       = "platypusClient.options"; //NOI18N
    public static final String PLATYPUS_CLIENT_LOG_LEVEL_KEY     = "platypusClient.logLevel"; //NOI18N
    public static final String PLATYPUS_CLIENT_VM_OPTIONS_KEY    = "PlatypusClient.vmOptions"; //NOI18N
    public static final String PLATYPUS_CLIENT_DEBUG_PORT_KEY    = "platypusClient.debugPort"; //NOI18N
    public static final String PLATYPUS_CLIENT_RUN_COMMAND_KEY   = "platypusClient.runCommand"; //NOI18N
    public static final String PLATYPUS_SERVER_OPTIONS_KEY       = "platypusServer.options"; //NOI18N
    public static final String PLATYPUS_SERVER_LOG_LEVEL_KEY     = "platypusServer.logLevel"; //NOI18N
    public static final String PLATYPUS_SERVER_VM_OPTIONS_KEY    = "platypusServer.vmOptions"; //NOI18N
    public static final String PLATYPUS_SERVER_DEBUG_PORT_KEY    = "platypusServer.debugPort"; //NOI18N
    public static final String START_LOCAL_PLATYPUS_SERVER_KEY   = "platypusServer.startLocal"; //NOI18N
    public static final String PLATYPUS_SERVER_RUN_COMMAND_KEY   = "platypusServer.runCommand"; //NOI18N
    public static final String SERVLET_CONTAINER_LOG_LEVEL_KEY   = "servletContainer.logLevel"; //NOI18N
    public static final String SERVLET_CONTAINER_DEBUG_PORT_KEY  = "servletContainer.debugPort"; //NOI18N
    public static final String START_LOCAL_SERVLET_CONTAINER_KEY = "servletContainer.startLocal"; //NOI18N
    public static final String SERVLET_CONTAINER_RUN_COMMAND_KEY = "servletContainer.runCommand"; //NOI18N
    protected static final String START_JS_FILE_TEMPLATE = "" //NOI18N
            + "/**\n" //NOI18N
            + " * Do not edit this file manually, it will be overwritten by\n" //NOI18N
            + " * Platypus Application Designer.\n" //NOI18N
            + " */\n"
            + "require(['%s', 'logger'], function (F, Logger) {\n"
            + "    var global = this;\n" //NOI18N
            + "    %sF.cacheBust(true);\n"
            + "    %sF.export(global);\n"
            + "    require('%s', function(%s){\n" //NOI18N
            + "%s"//NOI18N
            + "%s"//NOI18N
            + "    }, function(e){\n" //NOI18N
            + "        Logger.severe(e);\n"
            + "        if(global.document){\n"
            + "            var messageParagraph = global.document.createElement('p');\n"
            + "            global.document.body.appendChild(messageParagraph);\n"
            + "            messageParagraph.innerHTML = 'An error occured while require(\\'%s\\'). Error: ' + e;\n"
            + "            messageParagraph.style.margin = '10px';\n"
            + "            messageParagraph.style.fontFamily = 'Arial';\n"
            + "            messageParagraph.style.fontSize = '14pt';\n"
            + "        }\n" //NOI18N
            + "    });\n"//NOI18N
            + "});";

    protected final FileObject projectDir;
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected final EditableProperties projectProperties;
    protected final EditableProperties projectCommands;
    protected final EditableProperties projectPrivateProperties;
    private boolean projectCommandsDirty;
    private boolean projectPropertiesDirty;
    private boolean projectPrivatePropertiesDirty;

    public PlatypusProjectSettingsImpl(FileObject aProjectDir) throws Exception {
        if (aProjectDir == null) {
            throw new IllegalArgumentException("Project directory file object can't be null."); //NOI18N
        }
        projectDir = aProjectDir;
        projectCommands = new EditableProperties(false);
        projectProperties = new EditableProperties(false);
        projectPrivateProperties = new EditableProperties(false);
        load();
    }

    @Override
    public EditableProperties getProjectProperties() {
        return projectProperties;
    }

    @Override
    public EditableProperties getProjectPrivateProperties() {
        return projectProperties;
    }

    @Override
    public final void load() throws IOException {
        projectCommands.clear();
        try (InputStream is = getProjectCommandsFileObject().getInputStream()) {
            projectCommands.load(is);
        }
        projectProperties.clear();
        try (InputStream is = getProjectSettingsFileObject().getInputStream()) {
            projectProperties.load(is);
        }
        projectPrivateProperties.clear();
        try (InputStream is = getProjectPrivateSettingsFileObject().getInputStream()) {
            projectPrivateProperties.load(is);
        }
    }

    /**
     * Gets the project's display name.
     *
     * @return title for the project
     */
    @Override
    public String getDisplayName() {
        String displayName = projectProperties.get(PROJECT_DISPLAY_NAME_KEY);
        return displayName != null && !displayName.isEmpty() ? displayName : projectDir.getName();
    }

    /**
     * Sets the project's display name.
     *
     * @param aValue title for the project
     */
    @Override
    public void setDisplayName(String aValue) {
        if (aValue == null) {
            throw new NullPointerException("The Display name parameter cannot be null."); // NOI18N
        }
        String oldValue = getDisplayName();
        projectProperties.setProperty(PROJECT_DISPLAY_NAME_KEY, aValue);
        projectPropertiesDirty = true;
        changeSupport.firePropertyChange(PROJECT_DISPLAY_NAME_KEY, oldValue, aValue);
    }

    /**
     * Gets default application element to run.
     *
     * @return application element name
     */
    @Override
    public String getRunElement() {
        return projectProperties.get(RUN_ELEMENT_KEY);
    }

    /**
     * Sets default application element to run.
     *
     * @param aValue application element name
     * @throws java.lang.Exception
     */
    @Override
    public void setRunElement(String aValue) throws Exception {
        String oldValue = getRunElement();
        if (oldValue == null ? aValue != null : !oldValue.equals(aValue)) {
            if (aValue != null && !aValue.isEmpty()) {
                projectProperties.setProperty(RUN_ELEMENT_KEY, aValue);
            } else {
                projectProperties.remove(RUN_ELEMENT_KEY);
            }
            projectPropertiesDirty = true;
            changeSupport.firePropertyChange(RUN_ELEMENT_KEY, oldValue, aValue);
        }
    }

    @Override
    public String getSourcePath() {
        return projectProperties.get(SOURCE_PATH_KEY);
    }

    @Override
    public void setSourcePath(String aValue) {
        String oldValue = getSourcePath();
        if (oldValue == null ? aValue != null : !oldValue.equals(aValue)) {
            if (aValue != null && !aValue.isEmpty()) {
                projectProperties.setProperty(SOURCE_PATH_KEY, aValue);
            } else {
                projectProperties.remove(SOURCE_PATH_KEY);
            }
            projectPropertiesDirty = true;
            changeSupport.firePropertyChange(SOURCE_PATH_KEY, oldValue, aValue);
        }
    }

    /**
     * Get the default data source name
     *
     * @return string of the default data source name
     */
    @Override
    public String getDefaultDataSourceName() {
        return projectPrivateProperties.get(DEFAULT_DATA_SOURCE_ELEMENT_KEY);
    }

    /**
     * Sets the default data source name for a project
     *
     * @param aValue a default data source name
     */
    @Override
    public void setDefaultDatasourceName(String aValue) {
        String oldValue = getDefaultDataSourceName();
        if (aValue != null) {
            projectPrivateProperties.setProperty(DEFAULT_DATA_SOURCE_ELEMENT_KEY, aValue);
        } else {
            projectPrivateProperties.remove(DEFAULT_DATA_SOURCE_ELEMENT_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(DEFAULT_DATA_SOURCE_ELEMENT_KEY, oldValue, aValue);
    }

    /**
     * Gets username for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    @Override
    public String getRunUser() {
        return projectPrivateProperties.get(RUN_USER_NAME_KEY);
    }

    /**
     * Sets username for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    @Override
    public void setRunUser(String aValue) {
        String oldValue = getRunUser();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_USER_NAME_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_USER_NAME_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(RUN_USER_NAME_KEY, oldValue, aValue);
    }

    /**
     * Gets password for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    @Override
    public String getRunPassword() {
        return projectPrivateProperties.get(RUN_USER_PASSWORD_KEY);
    }

    /**
     * Sets password for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    @Override
    public void setRunPassword(String aValue) {
        String oldValue = getRunPassword();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_USER_PASSWORD_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_USER_PASSWORD_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(RUN_USER_PASSWORD_KEY, oldValue, aValue);
    }

    /**
     * Gets optional parameters provided to Platypus Client.
     *
     * @return parameters string
     */
    @Override
    public String getPlatypusClientOptions() {
        return projectPrivateProperties.get(PLATYPUS_CLIENT_OPTIONS_KEY);
    }

    /**
     * Sets optional parameters provided to Platypus Client.
     *
     * @param aValue
     */
    @Override
    public void setPlatypusClientOptions(String aValue) {
        String oldValue = getPlatypusClientOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_CLIENT_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(PLATYPUS_CLIENT_OPTIONS_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_CLIENT_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets JVM options provided to Platypus Client.
     *
     * @return parameters string
     */
    @Override
    public String getPlatypusClientVmOptions() {
        return projectPrivateProperties.get(PLATYPUS_CLIENT_VM_OPTIONS_KEY);
    }

    /**
     * Sets JVM options provided to Platypus Client.
     *
     * @param aValue
     */
    @Override
    public void setPlatypusClientVmOptions(String aValue) {
        String oldValue = getPlatypusClientVmOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_CLIENT_VM_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(PLATYPUS_CLIENT_VM_OPTIONS_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_CLIENT_VM_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets optional parameters provided to Platypus Application Server.
     *
     * @return parameters string
     */
    @Override
    public String getPlatypusServerOptions() {
        return projectPrivateProperties.get(PLATYPUS_SERVER_OPTIONS_KEY);
    }

    /**
     * Sets optional parameters provided to Platypus Application Server.
     *
     * @param aValue
     */
    @Override
    public void setPlatypusServerOptions(String aValue) {
        String oldValue = getPlatypusServerOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_SERVER_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(PLATYPUS_SERVER_OPTIONS_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_SERVER_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets JVM options provided to Platypus Application Server.
     *
     * @return parameters string
     */
    @Override
    public String getPlatypusServerVmOptions() {
        return projectPrivateProperties.get(PLATYPUS_SERVER_VM_OPTIONS_KEY);
    }

    /**
     * Sets JVM options provided to Platypus Application Server.
     *
     * @param aValue
     */
    @Override
    public void setPlatypusServerVmOptions(String aValue) {
        String oldValue = getPlatypusServerVmOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_SERVER_VM_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(PLATYPUS_SERVER_VM_OPTIONS_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_SERVER_VM_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets application server's host.
     *
     * @return Url string
     */
    @Override
    public String getPlatypusClientUrl() {
        return projectPrivateProperties.get(PLATYPUS_CLIENT_URL_KEY);
    }

    /**
     * Sets application's server host.
     *
     * @param aValue Url string
     */
    @Override
    public void setPlatypusClientUrl(String aValue) {
        String oldValue = getPlatypusClientUrl();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_CLIENT_URL_KEY, aValue);
        } else {
            projectPrivateProperties.remove(PLATYPUS_CLIENT_URL_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_CLIENT_URL_KEY, oldValue, aValue);
    }

    @Override
    public int getPlatypusServerPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(PLATYPUS_SERVER_PORT_KEY), DEFAULT_PLATYPUS_SERVER_PORT);
    }

    /**
     * Sets application's server port.
     *
     * @param aValue server port
     */
    @Override
    public void setPlatypusServerPort(int aValue) {
        int oldValue = getPlatypusServerPort();
        projectPrivateProperties.setProperty(PLATYPUS_SERVER_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_SERVER_PORT_KEY, oldValue, aValue);
    }

    /**
     * Gets servlet container port.
     *
     * @return server port
     */
    @Override
    public int getServletContainerPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(SERVLET_CONTAINER_PORT_KEY), DEFAULT_SERVLET_CONTAINER_PORT);
    }

    @Override
    public void setServletContainerPort(int aValue) {
        int oldValue = getServletContainerPort();
        projectPrivateProperties.setProperty(SERVLET_CONTAINER_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(SERVLET_CONTAINER_PORT_KEY, oldValue, aValue);
    }

    /**
     * Checks if start local development application server on application run.
     *
     * @return true not to start server
     */
    @Override
    public boolean getStartLocalPlatypusServer() {
        String value = projectPrivateProperties.get(START_LOCAL_PLATYPUS_SERVER_KEY);
        return value != null ? Boolean.valueOf(value) : true;
    }

    /**
     * Sets flag to start local development application server on application
     * run.
     *
     * @param aValue true not to start server
     */
    @Override
    public void setStartLocalPlatypusServer(boolean aValue) {
        boolean oldValue = getStartLocalPlatypusServer();
        projectPrivateProperties.setProperty(START_LOCAL_PLATYPUS_SERVER_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(START_LOCAL_PLATYPUS_SERVER_KEY, oldValue, aValue);
    }

    /**
     * Checks if start local development servlet container on application run.
     *
     * @return true not to start server
     */
    @Override
    public boolean getStartLocalServletContainer() {
        String value = projectPrivateProperties.get(START_LOCAL_SERVLET_CONTAINER_KEY);
        return value != null ? Boolean.valueOf(value) : true;
    }

    /**
     * Sets flag to start local development servlet container server on
     * application run.
     *
     * @param aValue true not to start server
     */
    @Override
    public void setStartLocalServletContainer(boolean aValue) {
        boolean oldValue = getStartLocalServletContainer();
        projectPrivateProperties.setProperty(START_LOCAL_SERVLET_CONTAINER_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(START_LOCAL_SERVLET_CONTAINER_KEY, oldValue, aValue);
    }

    /**
     * Checks if datasources from designer should be placed into
     * project.properties
     *
     * @return true if datasources from designer should be placed into
     * project.properties
     */
    @Override
    public boolean getAcceptDesginerDatasources() {
        String value = projectPrivateProperties.get(ACCEPT_DESIGNER_DATASOURCES_KEY);
        return value != null ? Boolean.valueOf(value) : true;
    }

    /**
     * Sets flag to place datasources from designer into project.properties.
     *
     * @param aValue true if datasources from designer should be placed into
     * project.properties
     */
    @Override
    public void setAcceptDesignerDatasources(boolean aValue) {
        boolean oldValue = getStartLocalPlatypusServer();
        projectPrivateProperties.setProperty(ACCEPT_DESIGNER_DATASOURCES_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(ACCEPT_DESIGNER_DATASOURCES_KEY, oldValue, aValue);
    }

    @Override
    public boolean getBrowserCacheBusting() {
        String value = projectPrivateProperties.get(BROWSER_CACHE_BUSTING_KEY);
        return value != null ? Boolean.valueOf(value) : true;
    }

    @Override
    public boolean getGlobalAPI() {
        String value = projectProperties.get(GLOBAL_API_KEY);
        return value != null ? Boolean.valueOf(value) : false;
    }

    @Override
    public void setBrowserCacheBusting(boolean aValue) {
        boolean oldValue = getBrowserCacheBusting();
        projectPrivateProperties.put(BROWSER_CACHE_BUSTING_KEY, "" + aValue);
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(BROWSER_CACHE_BUSTING_KEY, oldValue, aValue);
    }

    @Override
    public void setGlobalAPI(boolean aValue) {
        boolean oldValue = getGlobalAPI();
        projectProperties.put(GLOBAL_API_KEY, "" + aValue);
        projectPropertiesDirty = true;
        changeSupport.firePropertyChange(GLOBAL_API_KEY, oldValue, aValue);
    }

    /**
     * Gets application's context name.
     *
     * @return The name of the context string
     */
    @Override
    public String getWebApplicationContext() {
        return projectProperties.get(WEB_APPLICATION_CONTEXT_KEY);
    }

    /**
     * Sets application's context name.
     *
     * @param aValue The name of the context string
     */
    @Override
    public void setWebApplicationContext(String aValue) {
        String oldValue = getWebApplicationContext();
        if (aValue != null) {
            projectProperties.setProperty(WEB_APPLICATION_CONTEXT_KEY, aValue);
        } else {
            projectProperties.remove(WEB_APPLICATION_CONTEXT_KEY);
        }
        projectPropertiesDirty = true;
        changeSupport.firePropertyChange(WEB_APPLICATION_CONTEXT_KEY, oldValue, aValue);
    }

    /**
     * Checks if security realm to be configured on J2EE server startup.
     *
     * @return true to enable configure security realm
     */
    @Override
    public boolean getSecurityRealmEnabled() {
        return Boolean.valueOf(projectPrivateProperties.get(ENABLE_SECURITY_REALM_KEY));
    }

    /**
     * Sets if security realm to be configured on J2EE server startup.
     *
     * @param aValue true to enable configure security realm
     */
    @Override
    public void setSecurityRealmEnabled(boolean aValue) {
        boolean oldValue = getSecurityRealmEnabled();
        projectPrivateProperties.setProperty(ENABLE_SECURITY_REALM_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(ENABLE_SECURITY_REALM_KEY, oldValue, aValue);
    }

    /**
     * Gets client type to be run.
     *
     * @return ClientType instance
     */
    @Override
    public ClientType getClientType() {
        ClientType val = ClientType.getById(projectPrivateProperties.get(CLIENT_TYPE_KEY));
        return val != null ? val : ClientType.WEB_BROWSER;
    }

    /**
     * Sets client type to be run.
     *
     * @param aValue ClientType instance
     */
    @Override
    public void setClientType(ClientType aValue) {
        ClientType oldValue = getClientType();
        if (aValue != null) {
            projectPrivateProperties.setProperty(CLIENT_TYPE_KEY, aValue.getId());
        } else {
            projectPrivateProperties.remove(CLIENT_TYPE_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(CLIENT_TYPE_KEY, oldValue, aValue);
    }

    /**
     * Gets application server type to be run.
     *
     * @return AppServerType instance
     */
    @Override
    public AppServerType getApplicationServerType() {
        AppServerType val = AppServerType.getById(projectPrivateProperties.get(SERVER_TYPE_KEY));
        return val != null ? val : AppServerType.SERVLET_CONTAINER;
    }

    /**
     * Sets application server type to be run.
     *
     * @param aValue AppServerType instance
     */
    @Override
    public void setApplicationServerType(AppServerType aValue) {
        AppServerType oldValue = getApplicationServerType();
        if (aValue != null) {
            projectPrivateProperties.setProperty(SERVER_TYPE_KEY, aValue.getId());
        } else {
            projectPrivateProperties.remove(SERVER_TYPE_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(SERVER_TYPE_KEY, oldValue, aValue);
    }

    @Override
    public void save() throws IOException {
        if (projectCommandsDirty) {
            try (OutputStream os = getProjectCommandsFileObject().getOutputStream()) {
                projectCommands.store(os);
            }
            projectCommandsDirty = false;
        }
        if (projectPropertiesDirty) {
            try (OutputStream os = getProjectSettingsFileObject().getOutputStream()) {
                projectProperties.store(os);
            }
            projectPropertiesDirty = false;
        }
        if (projectPrivatePropertiesDirty) {
            try (OutputStream os = getProjectPrivateSettingsFileObject().getOutputStream()) {
                projectPrivateProperties.store(os);
            }
            projectPrivatePropertiesDirty = false;
        }
    }

    @Override
    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    protected final FileObject getProjectCommandsFileObject() {
        FileObject fo = projectDir.getFileObject(PROJECT_COMMANDS_FILE);
        if (fo == null) {
            try {
                fo = projectDir.createData(PROJECT_COMMANDS_FILE);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return fo;
    }

    protected final FileObject getProjectSettingsFileObject() {
        FileObject fo = projectDir.getFileObject(PROJECT_SETTINGS_FILE);
        if (fo == null) {
            try {
                fo = projectDir.createData(PROJECT_SETTINGS_FILE);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return fo;
    }

    protected final FileObject getProjectPrivateSettingsFileObject() {
        FileObject fo = projectDir.getFileObject(PROJECT_PRIVATE_SETTINGS_FILE);
        if (fo == null) {
            try {
                fo = projectDir.createData(PROJECT_PRIVATE_SETTINGS_FILE);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return fo;
    }

    /**
     * Gets the log level for Platypus Client.
     *
     * @return Log level value
     */
    @Override
    public Level getPlatypusClientLogLevel() {
        String logLevel = projectPrivateProperties.get(PLATYPUS_CLIENT_LOG_LEVEL_KEY);
        if (logLevel == null || logLevel.isEmpty()) {
            return DEFAULT_LOG_LEVEL;
        }
        try {
            return Level.parse(logLevel);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
     * Sets a log level for Platypus Client.
     *
     * @param aValue Log level value
     */
    @Override
    public void setPlatypusClientLogLevel(Level aValue) {
        Level oldValue = getPlatypusClientLogLevel();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_CLIENT_LOG_LEVEL_KEY, aValue.getName());
        } else {
            projectPrivateProperties.remove(PLATYPUS_CLIENT_LOG_LEVEL_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_CLIENT_LOG_LEVEL_KEY, oldValue, aValue);
    }

    /**
     * Gets the log level for Platypus Server.
     *
     * @return Log level value
     */
    @Override
    public Level getPlatypusServerLogLevel() {
        String logLevel = projectPrivateProperties.get(PLATYPUS_SERVER_LOG_LEVEL_KEY);
        if (logLevel == null || logLevel.isEmpty()) {
            return DEFAULT_LOG_LEVEL;
        }
        try {
            return Level.parse(logLevel);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
     * Sets a log level for Platypus Server.
     *
     * @param aValue Log level value
     */
    @Override
    public void setPlatypusServerLogLevel(Level aValue) {
        Level oldValue = getPlatypusServerLogLevel();
        if (aValue != null) {
            projectPrivateProperties.setProperty(PLATYPUS_SERVER_LOG_LEVEL_KEY, aValue.getName());
        } else {
            projectPrivateProperties.remove(PLATYPUS_SERVER_LOG_LEVEL_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_SERVER_LOG_LEVEL_KEY, oldValue, aValue);
    }

    @Override
    public Level getServletContainerLogLevel() {
        String logLevel = projectPrivateProperties.get(SERVLET_CONTAINER_LOG_LEVEL_KEY);
        if (logLevel == null || logLevel.isEmpty()) {
            return DEFAULT_LOG_LEVEL;
        }
        try {
            return Level.parse(logLevel);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_LOG_LEVEL;
        }
    }

    @Override
    public void setServletContainerLogLevel(Level aValue) {
        Level oldValue = getServletContainerLogLevel();
        if (aValue != null) {
            projectPrivateProperties.setProperty(SERVLET_CONTAINER_LOG_LEVEL_KEY, aValue.getName());
        } else {
            projectPrivateProperties.remove(SERVLET_CONTAINER_LOG_LEVEL_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(SERVLET_CONTAINER_LOG_LEVEL_KEY, oldValue, aValue);
    }

    /**
     * Gets JPDA debugging port for Platypus Client on local computer on
     * development if null or empty, use default value.
     *
     * @return JPDA debugging port
     */
    @Override
    public int getPlatypusClientDebugPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(PLATYPUS_CLIENT_DEBUG_PORT_KEY), DEFAULT_PLATYPUS_CLIENT_DEBUG_PORT);
    }

    /**
     * Sets JPDA debugging port for Platypus Client on local computer on
     * development.
     *
     * @param aValue JPDA debugging port
     */
    @Override
    public void setPlatypusClientDebugPort(int aValue) {
        int oldValue = getPlatypusClientDebugPort();
        projectPrivateProperties.setProperty(PLATYPUS_CLIENT_DEBUG_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_CLIENT_DEBUG_PORT_KEY, oldValue, aValue);
    }

    /**
     * Gets JPDA debugging port for Platypus Application Server on local
     * computer on development if null or empty, use default value.
     *
     * @return JPDA debugging port
     */
    @Override
    public int getPlatypusServerDebugPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(PLATYPUS_SERVER_DEBUG_PORT_KEY), DEFAULT_PLATYPUS_SERVER_DEBUG_PORT);
    }

    /**
     * Sets JPDA debugging port for Platypus Application Server on local
     * computer on development.
     *
     * @param aValue JPDA debugging port
     */
    @Override
    public void setPlatypusServerDebugPort(int aValue) {
        int oldValue = getPlatypusServerDebugPort();
        projectPrivateProperties.setProperty(PLATYPUS_SERVER_DEBUG_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_SERVER_DEBUG_PORT_KEY, oldValue, aValue);
    }

    @Override
    public int getServletContainerDebugPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(SERVLET_CONTAINER_DEBUG_PORT_KEY), DEFAULT_SERVLET_CONTAINER_DEBUG_PORT);
    }

    @Override
    public void setServletContainerDebugPort(int aValue) {
        int oldValue = getServletContainerDebugPort();
        projectPrivateProperties.setProperty(SERVLET_CONTAINER_DEBUG_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(SERVLET_CONTAINER_DEBUG_PORT_KEY, oldValue, aValue);
    }

    @Override
    public boolean getAutoApplyWebSettings() {
        String sValue = projectProperties.getProperty(AUTO_APPLY_WEB_XML_KEY);
        return sValue != null && !sValue.isEmpty() ? Boolean.valueOf(sValue) : true;
    }

    @Override
    public void setAutoApplyWebSettings(boolean aValue) {
        boolean oldValue = getAutoApplyWebSettings();
        projectProperties.setProperty(AUTO_APPLY_WEB_XML_KEY, aValue + "");
        projectPropertiesDirty = true;
        changeSupport.firePropertyChange(AUTO_APPLY_WEB_XML_KEY, oldValue, aValue);
    }

    @Override
    public String getCleanCommand() {
        return projectCommands.getProperty(CLEAN_COMMAND_KEY);
    }

    @Override
    public void setCleanCommand(String aValue) {
        String oldValue = getCleanCommand();
        if (aValue != null && !aValue.isEmpty()) {
            projectCommands.setProperty(CLEAN_COMMAND_KEY, aValue);
        } else {
            projectCommands.remove(CLEAN_COMMAND_KEY);
        }
        projectCommandsDirty = true;
        changeSupport.firePropertyChange(CLEAN_COMMAND_KEY, oldValue, aValue);
    }

    @Override
    public String getBuildCommand() {
        return projectCommands.getProperty(BUILD_COMMAND_KEY);
    }

    @Override
    public void setBuildCommand(String aValue) {
        String oldValue = getBuildCommand();
        if (aValue != null && !aValue.isEmpty()) {
            projectCommands.setProperty(BUILD_COMMAND_KEY, aValue);
        } else {
            projectCommands.remove(BUILD_COMMAND_KEY);
        }
        projectCommandsDirty = true;
        changeSupport.firePropertyChange(BUILD_COMMAND_KEY, oldValue, aValue);
    }

    @Override
    public String getPlatypusServerRunCommand() {
        return projectCommands.getProperty(PLATYPUS_SERVER_RUN_COMMAND_KEY);
    }

    @Override
    public void setPlatypusServerRunCommand(String aValue) {
        String oldValue = getPlatypusServerRunCommand();
        if (aValue != null && !aValue.isEmpty()) {
            projectCommands.setProperty(PLATYPUS_SERVER_RUN_COMMAND_KEY, aValue);
        } else {
            projectCommands.remove(PLATYPUS_SERVER_RUN_COMMAND_KEY);
        }
        projectCommandsDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_SERVER_RUN_COMMAND_KEY, oldValue, aValue);
    }

    @Override
    public String getServletContainerRunCommand() {
        return projectCommands.getProperty(SERVLET_CONTAINER_RUN_COMMAND_KEY);
    }

    @Override
    public void setServletContainerRunCommand(String aValue) {
        String oldValue = getServletContainerRunCommand();
        if (aValue != null && !aValue.isEmpty()) {
            projectCommands.setProperty(SERVLET_CONTAINER_RUN_COMMAND_KEY, aValue);
        } else {
            projectCommands.remove(SERVLET_CONTAINER_RUN_COMMAND_KEY);
        }
        projectCommandsDirty = true;
        changeSupport.firePropertyChange(SERVLET_CONTAINER_RUN_COMMAND_KEY, oldValue, aValue);
    }

    @Override
    public String getPlatypusClientRunCommand() {
        return projectCommands.getProperty(PLATYPUS_CLIENT_RUN_COMMAND_KEY);
    }

    @Override
    public void setPlatypusClientRunCommand(String aValue) {
        String oldValue = getPlatypusClientRunCommand();
        if (aValue != null && !aValue.isEmpty()) {
            projectCommands.setProperty(PLATYPUS_CLIENT_RUN_COMMAND_KEY, aValue);
        } else {
            projectCommands.remove(PLATYPUS_CLIENT_RUN_COMMAND_KEY);
        }
        projectCommandsDirty = true;
        changeSupport.firePropertyChange(PLATYPUS_CLIENT_RUN_COMMAND_KEY, oldValue, aValue);
    }

    @Override
    public String getBrowserCustomUrl() {
        return projectPrivateProperties.get(BROWSER_CUSTOM_URL_KEY);
    }

    @Override
    public void setBrowserCustomUrl(String aValue) {
        String oldValue = getBrowserCustomUrl();
        if (aValue != null) {
            projectPrivateProperties.setProperty(BROWSER_CUSTOM_URL_KEY, aValue);
        } else {
            projectPrivateProperties.remove(BROWSER_CUSTOM_URL_KEY);
        }
        projectPrivatePropertiesDirty = true;
        changeSupport.firePropertyChange(BROWSER_CUSTOM_URL_KEY, oldValue, aValue);
    }

    @Override
    public String getBrowserRunCommand() {
        return projectCommands.getProperty(BROWSER_RUN_COMMAND_KEY);
    }

    @Override
    public void setBrowserRunCommand(String aValue) {
        String oldValue = getBrowserRunCommand();
        if (aValue != null && !aValue.isEmpty()) {
            projectCommands.setProperty(BROWSER_RUN_COMMAND_KEY, aValue);
        } else {
            projectCommands.remove(BROWSER_RUN_COMMAND_KEY);
        }
        projectCommandsDirty = true;
        changeSupport.firePropertyChange(BROWSER_RUN_COMMAND_KEY, oldValue, aValue);
    }
}
