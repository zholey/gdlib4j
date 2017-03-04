# gdlib4j
这是一组常用组件的集合，满足Java日常编程。

一、工具类\n
1、EventDispatcher 类
提供事件中转派发功能，最初设计时用在Swing编程环境。

用法示例：
EventDispatcher<EventListener, EventObject> evtDispatcher = new EventDispatcher<>();
......
evtDispatcher.addEventListener(new EventListener() {
  
  // 方法名可以随意指定，派发事件时可以按方法名进行派发
  public void onSomethingEventFired(EventObject event) {
    // TODO: here is your code
  }
});
......
a)、只派发（广播）某事件通知
evtDispatcher.dispatchEvent("someCustomEventName");
b)、异步派发（广播）事件通知
evtDispatcher.dispatchEvent("someCustomEventName", true);
c)、派发（广播）事件通知，同时发送事件对象
evtDispatcher.dispatchEvent("someCustomEventName", new EventObject());
d)、异步派发（广播）事件通知，同时发送事件对象
evtDispatcher.dispatchEvent("someCustomEventName", new EventObject(), true);

e)、向指定的监听器派发事件通知
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName");
e)、向指定的监听器异步派发事件通知
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName", true);
e)、向指定的监听器派发事件通知，同时发送事件对象
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName", new EventObject());
e)、向指定的监听器异步派发事件通知，同时发送事件对象
evtDispatcher.dispatchEvent(specialListener, "someCustomEventName", new EventObject(), true);
