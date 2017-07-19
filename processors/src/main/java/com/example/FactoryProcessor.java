package com.example;

import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by Shufeng.Wu on 2017/7/19.
 */

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    Elements elementUtils;
    private Messager messager;

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
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnv.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Factory.class);
        for (Element ele:elements){
            if (ele.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Only classes can be annotated with @%s", Factory.class.getSimpleName()), ele);
                return true;
            }
        }
        return false;
    }

    private boolean isValidClass(TypeElement typeElement) {
        TypeElement classElement = typeElement;

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("The class %s is not public.", classElement
                    .getQualifiedName().toString()), classElement);
            return false;
        }

        // Check if it's an abstract class
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(),
                    Factory.class.getSimpleName()), classElement);
            return false;
        }

        TypeMirror superClassType = classElement.getSuperclass();
        /*if (superClassType.getKind() == TypeKind.NONE) {
            // Basis class (java.lang.Object) reached, so exit
            messager.printMessage(Diagnostic.Kind.ERROR,String.format("The class %s annotated with @%s must inherit from %s",
                    classElement.getQualifiedName().toString(),
                    Factory.class.getSimpleName(),
                    item.getQualifiedFactoryGroupName());
            return false;
        }

        if (superClassType.toString().equals(
                item.getQualifiedFactoryGroupName())) {
            // Required super class found
            break;
        }*/

        return true;
    }


}
