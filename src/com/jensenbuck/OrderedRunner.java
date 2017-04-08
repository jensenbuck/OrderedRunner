package com.jensenbuck;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import javassist.ClassPool;
import javassist.NotFoundException;

public class OrderedRunner extends BlockJUnit4ClassRunner {
  
  private List<FrameworkMethod> methods = new ArrayList<>();
  
  public OrderedRunner(Class<?> clazz) throws NotFoundException, InstantiationException, IllegalAccessException, InitializationError {
    super(clazz);
    
    ClassPool pool = ClassPool.getDefault();
    TestClass testClass = new TestClass(clazz);
    Map<Integer, FrameworkMethod> methodMap = new HashMap<>();
    
    List<FrameworkMethod> frameworkMethods = testClass.getAnnotatedMethods(Test.class);
    
    for(FrameworkMethod f : frameworkMethods) {
      int modifiers = f.getMethod().getModifiers();
      
      if(!Modifier.isStatic(modifiers) && !Modifier.isAbstract(modifiers) && f.getMethod().getParameterTypes().length == 0) {
        methodMap.put(pool.getCtClass(clazz.getName()).getDeclaredMethod(f.getName()).getMethodInfo().getLineNumber(0), f);
      }
    }
    
    List<Integer> lineNumbers = new ArrayList<>(methodMap.keySet());
    Collections.sort(lineNumbers);
    
    for(int i : lineNumbers) {
      methods.add(methodMap.get(i));
    }
  }
  
  @Override
  protected List<FrameworkMethod> getChildren() {
    return methods;
  }

}
