package com.example;

import com.google.auto.service.AutoService;
import com.google.common.reflect.Parameter;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Shufeng.Wu on 2017/7/19.
 */

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    /*private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;*/
    private Map<String, Class<?>> map =
            new LinkedHashMap<>();
    private ProcessingEnvironment processingEnv;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(Factory.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        /*typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();*/


    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        map.clear();
        for (Element annotatedElement : roundEnvironment
                .getElementsAnnotatedWith(Factory.class)) {
            /*if (annotatedElement.getKind() != ElementKind.CLASS) {
                return true; // Exit processing
            }*/

            // We can cast it, because we know that it of ElementKind.CLASS
            TypeElement typeElement = (TypeElement) annotatedElement;
            Factory factory = typeElement.getAnnotation(Factory.class);
            Map<String, Class<?>> map = new LinkedHashMap<>();
            try {
                map.put(factory.id(), factory.type());
            } catch (MirroredTypeException mte) {
            }
        }
        generateCode(map,processingEnv);
        return false;
    }

    public void generateCode(Map<String, Class<?>> map,ProcessingEnvironment processingEnv){
        ParameterSpec id = ParameterSpec.builder(String.class,"id").build();
        MethodSpec.Builder createBuilder = MethodSpec.methodBuilder("create")
                .addParameter(id)
                .addStatement("if (null == $L) {\n" +
                        "throw new IllegalArgumentException(\"name of meal is null!\");\n" +
                        "}\n",id);
        for (String key:map.keySet()){
            createBuilder.addStatement("if ($S.equals($L)) {\n" +
                    "return new $T();\n" +
                    "}\n",key,id,map.get(key));
        }
        createBuilder.addStatement("throw new IllegalArgumentException(\"Unknown meal '\" + $L + \"'\")",id)
        .addModifiers(Modifier.PUBLIC);
        MethodSpec create = createBuilder.build();
        TypeSpec mealFactory = TypeSpec.classBuilder("MealFactory").addModifiers(Modifier.PUBLIC)
                .addMethod(create).build();
        JavaFile javaFile = JavaFile.builder("com.delta.test.aptlearning", mealFactory)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();}

    }

}
