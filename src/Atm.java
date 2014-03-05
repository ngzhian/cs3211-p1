public class Atm extends Thread {
  Integer account;
  Integer amount;

  public Atm login(Integer account) {
    this.account = account;
    return this;
  }

  public void setWithdrawAmount(Integer amount) {
    this.amount = amount;
  }

  @Override
  public void run() {
    if (account == null || amount == null) {
      return;
    }
    ProcessingUnit pu = new ProcessingUnit(this.account);
    pu.setWithdrawAmount(amount);
    pu.start();
    try {
      pu.join();
      if (pu.hasFailed()) {
        System.out.println("atm fail");
      } else {
        System.out.println("atm success");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
