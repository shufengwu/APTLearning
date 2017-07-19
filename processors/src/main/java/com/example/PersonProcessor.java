package com.example;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class PersonProcessor extends AbstractProcessor {

    Elements elementUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(Person.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Person.class);
        TypeElement classElement = null;
        // 存放二者
        Map<String, List<VariableElement>> maps = new HashMap<String, List<VariableElement>>();

        for (Element element : elements) {
            //判断元素是否为类
            if (element.getKind() == ElementKind.CLASS) {
                //获取类元素
                classElement = (TypeElement) element;

                //获取类元素中的所有元素
                List<? extends Element> allMembers = elementUtils.getAllMembers(classElement);
                if(allMembers.size()>0){
                    //ElementFilter.fieldsIn(allMembers)获取元素中的field元素
                    maps.put(classElement.getQualifiedName().toString(),ElementFilter.fieldsIn(allMembers));
                }
                //判断该元素是否为成员变量
            } else if (element.getKind() == ElementKind.FIELD) {

                //获取成员变量元素
                VariableElement variableElement = (VariableElement) element;

                //获取封装类
                TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();

                List<VariableElement> members = null;
                if(!maps.containsKey(typeElement.getQualifiedName().toString())){
                    members = new ArrayList<>();
                    members.add(variableElement);
                }else{
                    members = maps.get(typeElement.getQualifiedName().toString());
                    members.add(variableElement);
                }
                maps.put(typeElement.getQualifiedName().toString(),members);

            } else {

            }
        }
        generateCodes(maps);
        return true;
    }

    private void generateCodes(Map<String, List<VariableElement>> maps) {
        File dir = new File("f://apt_test");
        if (!dir.exists())
            dir.mkdirs();

        // 遍历map
        for (String key : maps.keySet()) {

            // 创建文件
            File file = new File(dir, key.replaceAll("\\.", "_") + ".json");
            try {

                /**
                 * 编写json文件内容
                 */

                FileWriter fw = new FileWriter(file);
                fw.append("{").append("class:").append("\"" + key + "\"")
                        .append(",\n ");
                fw.append("fields:\n {\n");
                List<VariableElement> fields = maps.get(key);
                for (int i = 0; i < fields.size(); i++) {
                    VariableElement field = fields.get(i);
                    fw.append("  ").append(field.getSimpleName()).append(":")
                            .append("\"" + field.asType().toString() + "\"");
                    if (i < fields.size() - 1) {
                        fw.append(",");
                        fw.append("\n");
                    }
                }
                fw.append("\n }\n");
                fw.append("}");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
