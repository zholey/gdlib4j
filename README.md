# gdlib4j

> 这是一组常用组件的集合，满足Java日常编程。



## 一、工具类

### 1、EventDispatcher

> 提供事件中转派发功能，实现了“观察者模式”；
>
> 最初设计是用于Swing编程环境。事实上这个类是通用的，可适用于任何场景。



用法示例：

```java
EventDispatcher<EventListener, EventObject> evtDispatcher = new EventDispatcher<>();

//......
    
evtDispatcher.addEventListener(new EventListener() {

  // 方法名可以随意指定，派发事件时可以按方法名进行派发
  public void onSomethingEventFired(EventObject event) {
    // TODO: here is your code
  }
});

//......
    
// 只派发（广播）某事件通知
evtDispatcher.dispatchEvent("someCustomEventName");
// 异步派发（广播）事件通知
evtDispatcher.dispatchEvent("someCustomEventName", true);
// 派发（广播）事件通知，同时发送事件对象
evtDispatcher.dispatchEvent("someCustomEventName", new EventObject());
// 异步派发（广播）事件通知，同时发送事件对象
evtDispatcher.dispatchEvent("someCustomEventName", new EventObject(), true);

// 向指定的监听器派发事件通知
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName");
// 向指定的监听器异步派发事件通知
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName", true);
// 向指定的监听器派发事件通知，同时发送事件对象
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName", new EventObject());
// 向指定的监听器异步派发事件通知，同时发送事件对象
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName", new EventObject(), true);
```

