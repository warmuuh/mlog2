package mlog.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Counter {

  private final long windowInMs;

  private long startTime = -1;
  private long curWindowCount = 0;
  private long prevWindowRate = -1;

  public void count(int elements){

    curWindowCount += elements;

    refreshRate();
  }


  public long getRate(){
    refreshRate();
    if (prevWindowRate < 0)
      return curWindowCount;

    return prevWindowRate;
  }

  private void refreshRate() {
    if (this.startTime < 0){
      this.startTime = System.currentTimeMillis();
    }

    if (System.currentTimeMillis() - startTime > windowInMs){
      startTime = System.currentTimeMillis();
      prevWindowRate = curWindowCount;
      curWindowCount = 0;
    }
  }
}
