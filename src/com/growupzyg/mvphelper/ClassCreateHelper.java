package com.growupzyg.mvphelper;

import com.intellij.execution.ui.layout.View;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**创建class的类
 * Created by wing on 2016/7/23.
 */
public class ClassCreateHelper {
    static final int  PRESENTER = 1;
    static final int VIEW = 2;

    /**
     * 创建实现类
     * @param path
     * @param className
     * @param classFullName
     * @param mode
     * @throws IOException
     */
    public static void createClasses(String path, String className, String classFullName, int mode) throws IOException {
        String type = null;
        if(mode == PRESENTER){
            type = "Presenter";
        }else if(mode == VIEW){
            type = "Fragment";
        }
        String dir = path;

        String filePath = dir + className + type+".java";

        File dirs = new File(dir);

        System.out.println("dirs = "+dir);
        File file = new File(filePath);
        if(!dirs.exists()){
            dirs.mkdir();
        }
        file.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String packageName = getPackageName(dir);
        writer.write("package "+ packageName+";");
        writer.newLine();
        writer.newLine();
        if(mode == VIEW){
            writer.write("import android.support.v4.app.Fragment;");
            writer.newLine();
            writer.newLine();
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        writer.write("/**\n* Created by MVPHelper on "+sdf.format(date)+"\n*/");

        writer.newLine();
        writer.newLine();
        if(mode == PRESENTER){
            writer.write("public class " + className + "Presenter implements "+classFullName+".Presenter"+" {");
            writer.newLine();
            writer.newLine();
            writer.write("\tprivate final "+classFullName+".View m"+className+"View;");
            writer.newLine();
            writer.newLine();
            writer.write("\tpublic "+className+"Presenter("+classFullName+".View "+getFirstLowerVariables(className)+"View) {m"+className+"View = "+getFirstLowerVariables(className)+"View;}");
            writer.newLine();
            writer.write("\t@Override\n");
            writer.write("\tpublic void start() {\n");
            writer.write("\t\t\n");
            writer.write("\t}");
        }else if(mode == VIEW){
            writer.write("public class "+className+"Fragment extends Fragment implements "+classFullName+".View {");
            writer.newLine();
            writer.newLine();
            writer.write("\tprivate "+classFullName+".Presenter mPresenter;");
            writer.newLine();
            writer.newLine();
            writer.write("\t@Override\n");
            writer.write("\tpublic void setPresenter("+classFullName+".Presenter presenter) {\n");
            writer.write("\t\tmPresenter = presenter;\n");
            writer.write("\t}");
        }

        writer.newLine();
        writer.newLine();
        writer.write("}");

        writer.flush();
        writer.close();
    }

    public static String getPackageName(String path) {
        String[] strings = path.split("/");
        StringBuilder packageName = new StringBuilder();
        int index = 0;
        int length = strings.length;
        for(int i = 0;i<strings.length;i++){
            if(strings[i].equals("cn")||strings[i].equals("com")||strings[i].equals("org")){
                index = i;
                break;
            }

        }
        for(int j = index;j<length;j++){
            packageName.append(strings[j]+".");
        }
        String finalPackageName = packageName.toString();
        finalPackageName = finalPackageName.substring(0,finalPackageName.lastIndexOf("."));
        return finalPackageName;
    }

    public static String getCurrentPath(AnActionEvent e,String classFullName){

        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        String path = currentFile.getPath().replace(classFullName+".java","");
        return path;
    }

    /**
     * 得到首字母大写的变量名称
     * @param variables
     * @return
     */
    private static String getFirstLowerVariables(String variables){
        String firstLetter = variables.substring(0,1);
        variables = variables.replaceFirst(firstLetter,firstLetter.toLowerCase());
        return variables;
    }



}
