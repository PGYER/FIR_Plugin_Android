package ro.catalin.prata.firuploader.controller;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.android.dom.AndroidAttributeValue;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.facet.AndroidRootUtil;

public class ModulesManager {

    public static final String ANDROID_VERSION_CODE = "android:versionCode";
    public static final String ANDROID_VERSION_NAME = "android:versionName";
    public static final String ANDROID_APP_ID = "package";
    public static final String ANDROID_APP_NAME = "android:label";
    /**
     * Manager's single instance
     */
    private static ModulesManager sInstance = null;

    private ModulesManager() {

    }

    public static ModulesManager instance() {

        if (sInstance == null) {
            sInstance = new ModulesManager();
        }

        return sInstance;
    }

    /**
     * Returns the given module's apk path
     *
     * @param module android module used to get the facet and the apk file path
     * @return file path of the android apk for the given module
     */
    public String getAndroidApkPath(Module module) {

        if (module == null || AndroidFacet.getInstance(module) == null) {
            return null;
        }

        return AndroidRootUtil.getApkPath(AndroidFacet.getInstance(module));

    }

    /**
     * return the given manifest Path
     * @param module
     * @return
     */
    public String getAndroidManifestPath(Module module) {

        if (module == null || AndroidFacet.getInstance(module) == null) {
            return null;
        }

        return AndroidRootUtil.getManifestFile(AndroidFacet.getInstance(module)).getPath();

    }
    /**
     * Returns an array of module names for the current project
     *
     * @return array of module names
     */
    public String[] getAllModuleNames() {

        Module[] modules = getModules();

        if (modules == null) {
            return null;
        }

        String[] moduleNames = new String[modules.length];

        int index = 0;
        for (Module module : modules) {

            moduleNames[index] = module.getName();
            index++;
        }

        return moduleNames;

    }

    /**
     * Returns all modules found in the main project,
     * which is the first opened project if there are more than one projects opened at a time
     *
     * @return array of modules for the main project
     */
    public Module[] getModules() {
        Module[] modules = ModuleManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getSortedModules();
        Module[] sortedModules = new Module[modules.length];

        // used to go back from the last module to the first one
        int reverseIndex = modules.length - 1;

        // loop through all the modules and add the in a reverse order in the sorted array
        for (int index = 0; index < modules.length; index++) {
            sortedModules[index] = modules[reverseIndex];
            reverseIndex--;
        }

        return sortedModules;
    }

    /**
     * Returns the index of the selected module in the project modules list, if the module is not present here, 0 is returned
     *
     * @param moduleName module name
     * @return 0 if the module is not in the list of the current project
     */
    public int getSelectedModuleIndex(String moduleName) {
        Module[] modules = getModules();

        if (modules == null || moduleName == null) {
            return 0;
        }

        int index = 0;
        for (Module module : modules) {

            if (module.getName().equals(moduleName)) {
                return index;
            }

            index++;
        }

        return 0;
    }

    /**
     * Returns the module that is not(or the less) used by the other modules, this way we avoid getting library modules
     *
     * @return the most important module, which can be a non library module
     */
    public Module getMostImportantModule() {

        Module[] modules = getModules();

        if (modules == null) {
            return null;
        } else {

            // the last one is the one that is not used by the other modules
            // or is the most absent from the other modules dependency
            return modules[0];

        }

    }

    /**
     * Returns a module from the main project that has the given name
     *
     * @return module with the given name or null if not found in this project
     */
    public Module getModuleByName(String moduleName) {
        Module[] modules = getModulesForProject(ProjectManager.getInstance().getOpenProjects()[0]);

        if (modules == null) {
            return null;
        }

        for (Module module : modules) {

            if (module.getName().equals(moduleName)) {
                return module;
            }

        }

        return null;

    }

    /**
     * Returns an array of module names for the current project
     *
     * @return array of module names
     */
    @Deprecated
    public String[] getAllModuleNamesForCurrentProject(Project project) {

        Module[] modules = getModulesForProject(project);
        String[] moduleNames = new String[modules.length];

        int index = 0;
        for (Module module : modules) {

            moduleNames[index] = module.getName();
            index++;
        }

        return moduleNames;

    }

    /**
     * Returns all modules found in the main project,
     * which is the first opened project if there are more than one projects opened at a time
     *
     * @return array of modules for the main project
     */
    @Deprecated
    public Module[] getModulesForProject(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getSortedModules();
        Module[] sortedModules = new Module[modules.length];

        // used to go back from the last module to the first one
        int reverseIndex = modules.length - 1;

        // loop through all the modules and add the in a reverse order in the sorted array
        for (int index = 0; index < modules.length; index++) {
            sortedModules[index] = modules[reverseIndex];
            reverseIndex--;
        }

        return sortedModules;
    }

    /**
     * Returns the index of the selected module in the project modules list, if the module is not present here, 0 is returned
     *
     * @param moduleName module name
     * @return 0 if the module is not in the list of the current project
     */
    @Deprecated
    public int getSelectedModuleIndexForProject(String moduleName, Project project) {
        Module[] modules = getModulesForProject(project);

        if (modules == null) {
            return 0;
        }

        int index = 0;
        for (Module module : modules) {

            if (module.getName().equals(moduleName)) {
                return index;
            }

            index++;
        }

        return 0;
    }

    /**
     * Returns a module from the main project that has the given name
     *
     * @return module with the given name or null if not found in this project
     */
    @Deprecated
    public Module getModuleByName(String moduleName, Project project) {
        Module[] modules = getModulesForProject(project);

        if (modules == null) {
            return null;
        }

        for (Module module : modules) {

            if (module.getName().equals(moduleName)) {
                return module;
            }

        }

        return null;

    }

    /**
     * Returns the module that is not(or the less) used by the other modules, this way we avoid getting library modules
     *
     * @return the most important module, which can be a non library module
     */
    @Deprecated
    public Module getMostImportantModuleForProject(Project project) {

        Module[] modules = getModulesForProject(project);

        if (modules == null) {
            return null;
        } else {

            // the last one is the one that is not used by the other modules
            // or is the most absent from the other modules dependency
            return modules[modules.length - 1];

        }

    }

    /**
     * Returns the manifest file for the given module
     *
     * @param module module to search the manifest document for
     * @return manifest doc for the given module
     */
    public Manifest getManifestForModule(final Module module) {

        if (module == null || AndroidFacet.getInstance(module) == null) {
            return null;
        }

        return AndroidFacet.getInstance(module).getManifest();

    }

    /**
     * Returns the build version name from the given manifest file
     *
     * @param manifest manifest file that will be searched for the build version name
     * @return the current manifest version name value
     */
    public String getBuildVersionName(Manifest manifest) {

        if (manifest == null || manifest.getXmlTag() == null
                || manifest.getXmlTag().getAttribute(ANDROID_VERSION_NAME) == null) {
            return null;
        }

        return manifest.getXmlTag().getAttribute(ANDROID_VERSION_NAME).getValue();
    }

    public String getAppId(Manifest manifest){
        if (manifest == null || manifest.getXmlTag() == null
                || manifest.getXmlTag().getAttribute(ANDROID_APP_ID) == null) {
            return null;
        }

        return manifest.getXmlTag().getAttribute(ANDROID_APP_ID).getValue();
    }

    public String getAndroidAppName(Manifest manifest){
        if (manifest == null || manifest.getXmlTag() == null
                || manifest.getXmlTag().getAttribute(ANDROID_APP_NAME) == null) {
            return null;
        }


        String name;
        if(manifest!=null &&  manifest.getApplication()!=null && manifest.getApplication().getName()!=null){
            AndroidAttributeValue<PsiClass> t = manifest.getApplication().getName();
            if(t!=null) {
                PsiClass valur = t.getValue();
                if(valur!=null){
                    name = valur.getName();
                    return name;
                }   else return null;
            } else return null;


        }  else return null    ;


    }
    /**
     * Set the build version name in the given manifest file,
     * note that this action is not performed in a write action environment so it should be called inside an
     * ApplicationManager.getApplication().runWriteAction() method
     *
     * @param manifest manifest file that will be altered
     * @param newValue new version name value
     */
    private void setBuildVersionName(Manifest manifest, String newValue) {

        if (manifest != null && newValue != null && manifest.getXmlTag() != null
                && manifest.getXmlTag().getAttribute(ANDROID_VERSION_NAME) != null) {
            manifest.getXmlTag().getAttribute(ANDROID_VERSION_NAME).setValue(newValue);
        }

    }

    /**
     * Set the build version name and code in the manifest file asynchronously as it has to run in write mode
     *
     * @param manifest            manifest file to be altered
     * @param newVersionNameValue the new version name value
     * @param newVersionCodeValue the new version code value
     * @param delegate            if != null, this will send callbacks on completion
     */
    public void setBuildVersionNameAndCode(final Manifest manifest, final String newVersionNameValue,
                                           final String newVersionCodeValue, final ManifestChangesDelegate delegate) {

        if (manifest == null) {
            return;
        }

        // open a write action environment so we can update the manifest file
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {

                // set the version name value
                setBuildVersionName(manifest, newVersionNameValue);
                // set the version code value
                setBuildVersionCode(manifest, newVersionCodeValue);

                if (delegate != null) {

                    // notify that the values were changed
                    delegate.onVersionValueFinishedUpdate();

                }

            }
        });

    }

    /**
     * Set the build version code in the given manifest file,
     * note that this action is not performed in a write action environment so it should be called inside an
     * ApplicationManager.getApplication().runWriteAction() method
     *
     * @param manifest the project manifest file
     * @param newValue the value to be set as the version code
     */
    private void setBuildVersionCode(Manifest manifest, String newValue) {

        if (manifest != null && newValue != null && manifest.getXmlTag() != null
                && manifest.getXmlTag().getAttribute(ANDROID_VERSION_CODE) != null) {
            manifest.getXmlTag().getAttribute(ANDROID_VERSION_CODE).setValue(newValue);
        }

    }

    /**
     * Returns the code version of the given manifest file
     *
     * @param manifest android manifest dom object
     * @return android build version code
     */
    public String getBuildVersionCode(Manifest manifest) {

        if (manifest == null || manifest.getXmlTag() == null ||
                manifest.getXmlTag().getAttribute(ANDROID_VERSION_CODE) == null) {
            return null;
        }

        return manifest.getXmlTag().getAttribute(ANDROID_VERSION_CODE).getValue();
    }

    /**
     * Returns the opened projects
     *
     * @return the opened projects
     */
    public Project[] getOpenedProjects() {

        return ProjectManager.getInstance().getOpenProjects();

    }

    /**
     * Returns a list of Strings containing the names of the opened projects
     *
     * @return list of opened projects names
     */
    public String[] getOpenedProjectsNames() {

        Project[] projects = getOpenedProjects();

        String[] projectsNames = new String[projects.length];

        int index = 0;
        for (Project project : projects) {
            projectsNames[index] = project.getName();
            index++;
        }

        return projectsNames;

    }

    /**
     * Returns the project object that has the given name
     *
     * @param name project name to search for in the list of opened projects
     * @return project object or null if no project was found with the given name
     */
    public Project getProjectByName(String name) {

        if (name == null) {
            return null;
        }

        Project projects[] = ProjectManager.getInstance().getOpenProjects();

        for (Project project : projects) {
            if (project.getName().equals(name)) {
                return project;
            }
        }

        return null;

    }

    /**
     * Returns the index of the project that has the given name, the project name is searched in the list of opened projects
     *
     * @param name name of the project
     * @return the index of the project in the list of opened projects, 0 if not found
     */
    public int getProjectIndexWithName(String name) {
        int index = 0;
        Project projects[] = ProjectManager.getInstance().getOpenProjects();

        for (Project project : projects) {
            if (project.getName().equals(name)) {
                return index;
            }
            index++;
        }

        return 0;
    }

    /**
     * Used to notify manifest changes updates as each write action needs to be done on a secondary thread
     */
    public interface ManifestChangesDelegate {

        /**
         * Called after the version name and code values were changed in the manifest file
         */
        public void onVersionValueFinishedUpdate();

    }


}
