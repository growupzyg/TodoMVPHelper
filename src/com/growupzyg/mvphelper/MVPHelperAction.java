package com.growupzyg.mvphelper;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiJavaFile;
import org.apache.http.util.TextUtils;

import java.io.IOException;

/**
 * entrance file
 * Created by zyg on 16/09/07.
 */
public class MVPHelperAction extends AnAction {
    private ClassModel _classModel;
    private Editor _editor;
    private String _content;
    private boolean canCreate;
    private AnActionEvent _event;
    private String _path;
    private String mBasePackageName;//BaseView（or BasePresenter）'s package name
    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here

        this._event = e;
        canCreate = true;
        init(e);
        getClassModel();
        createFiles();
        PsiJavaFile javaFile = (PsiJavaFile) e.getData(CommonDataKeys.PSI_FILE);

        System.out.println("current package name is :"+javaFile.getPackageName());
        try {
            if(canCreate) {
                createClassFiles();
                MessagesCenter.showMessage("created success! please wait a moment","success");
                refreshProject(e);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false,true);
        }

    /**
     * create class files
     * @throws IOException
     */
    private void createClassFiles() throws IOException {
        createFileWithContract();
    }

    /**
     * create files with contract file
     * @throws IOException
     */
    private void createFileWithContract() throws IOException {
        String className = _classModel.get_className();
        String classFullName = _classModel.get_classFullName();
        System.out.println("_path:" + _path);
        //create Presenter
        ClassCreateHelper.createClasses(_path, className, classFullName, ClassCreateHelper.PRESENTER);
        //create View
        ClassCreateHelper.createClasses(_path, className, classFullName, ClassCreateHelper.VIEW);
    }


    /**
     * create Files
     */
    private void createFiles() {
        if (null == _classModel.get_className()) {
            return;
        }
         _path= ClassCreateHelper.getCurrentPath(_event,_classModel.get_classFullName());
        System.out.println("current _path"+ _path);
        if(_classModel.get_classFullName().contains("Contract")) {

            System.out.println("_path replace contract "+ _path);
            _path = _path.replace("contract/", "");
        } else {
            MessagesCenter.showErrorMessage("Your FileName should end with 'Contract'.", "error");
            canCreate = false;
        }
        if(canCreate) {
            setFileDocument();
        }

    }

    /**
     * create Contract Model Presenter
     */
    private void setFileDocument() {
        String packageName = ClassCreateHelper.getPackageName(_path);

        String packageInfo = "package "+ packageName+";\n";

        String importInfo = "import "+mBasePackageName+".BaseView;\n";
        importInfo += "import "+mBasePackageName+".BasePresenter;\n";

        int packageIndex = _content.indexOf(packageInfo)+packageInfo.length();
        int lastIndex = _content.lastIndexOf("}");
        _content = packageInfo+importInfo+_content.substring(packageIndex, lastIndex);
        MessagesCenter.showDebugMessage(_content, "debug");
        final String content = setContractContent();
        //wirte in runWriteAction
        WriteCommandAction.runWriteCommandAction(_editor.getProject(),
                new Runnable() {
                    @Override
                    public void run() {
                        _editor.getDocument().setText(content);
                    }
                });

    }

    private String setContractContent() {
        String content = _content + "\tinterface View extends BaseView<Presenter> {\n\t}\n\n"
                + "\tinterface Presenter extends BasePresenter {\n\t}\n"
                + "\n}";

        return content;
    }


    private void getClassModel() {
        _content = _editor.getDocument().getText();

        String[] words = _content.split(" ");

        for (String word : words) {
            if (word.contains("Contract")) {
                String className = word.substring(0, word.indexOf("Contract"));
                _classModel.set_className(className);
                _classModel.set_classFullName(word);
                MessagesCenter.showDebugMessage(className, "class name");
            }
        }
        if (null == _classModel.get_className()) {
            MessagesCenter.showErrorMessage("Create failed ,Can't found 'Contract' in your class name,your class name must contain 'Contract'", "error");
            canCreate = false;
        }else {
            mBasePackageName = PropertiesComponent.getInstance().getValue(SettingUI.BASE_PACKAGE_NAME);
            if(TextUtils.isEmpty(mBasePackageName)){
                MessagesCenter.showErrorMessage("Create failed ,package name can not be empty,you can set data in File-Setting-Other Settings-TodoMVP", "error");
                canCreate = false;
            }
        }
    }

    private void init(AnActionEvent e) {
        _editor = e.getData(PlatformDataKeys.EDITOR);
        _classModel = new ClassModel();
    }


}
