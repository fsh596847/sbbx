package com.zhaolong.android.sbbx.beans;

import java.io.Serializable;

public class Device implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -3308984675995606392L;
  private String equipId;
  private String equipCode;//设备编号
  private String equipSeq;//设备序列号
  private String equipName;
  private String equipType;
  private String hospital;
  private String depar;
  private String deparName;
  private String equipAddress;
  private int useState;
  private String operFirm;
  private String saleFirm;
  private String operCyc;
  private String equipBrand;
  private String engineerName;
  private String intDate;
  private int isAction;
  private String qc;
  private String scaling;
  private String id;
  private String engineerId;
  private String upUseCyc;
  private String equipBar;
  private String imgurl;
  private String equipClass;
  private String falultDesc;
  private String repairTime;
  private String repairName;
  private String bookingTime;
  private String equipment;
  private String orderid;
  private String comments;
  private int starnum;
  private int isevaluate;
  private String faultImg1;
  private String faultImg2;
  private String faultImg3;
  private int state;
  private String img;
  private String oldequipCode;//原设备编号
  private String outFactoryDate;
  private String tag;
  private String ordercode;
  private String orderName;

  public String getOrderName() {
    return orderName;
  }

  public void setOrderName(String orderName) {
    this.orderName = orderName;
  }

  public String getOrdercode() {
    return ordercode;
  }

  public void setOrdercode(String ordercode) {
    this.ordercode = ordercode;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getOutFactoryDate() {
    return outFactoryDate;
  }

  public void setOutFactoryDate(String outFactoryDate) {
    this.outFactoryDate = outFactoryDate;
  }

  public String getOldequipCode() {
    return oldequipCode;
  }

  public void setOldequipCode(String oldequipCode) {
    this.oldequipCode = oldequipCode;
  }

  public String getDeparName() {
    return deparName;
  }

  public void setDeparName(String deparName) {
    this.deparName = deparName;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public String getFaultImg1() {
    return faultImg1;
  }

  public void setFaultImg1(String faultImg1) {
    this.faultImg1 = faultImg1;
  }

  public String getFaultImg2() {
    return faultImg2;
  }

  public void setFaultImg2(String faultImg2) {
    this.faultImg2 = faultImg2;
  }

  public String getFaultImg3() {
    return faultImg3;
  }

  public void setFaultImg3(String faultImg3) {
    this.faultImg3 = faultImg3;
  }

  public int getIsevaluate() {
    return isevaluate;
  }

  public void setIsevaluate(int isevaluate) {
    this.isevaluate = isevaluate;
  }

  public int getStarnum() {
    return starnum;
  }

  public void setStarnum(int starnum) {
    this.starnum = starnum;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getEquipCode() {
    return equipCode;
  }

  public void setEquipCode(String equipCode) {
    this.equipCode = equipCode;
  }

  public String getOrderid() {
    return orderid;
  }

  public void setOrderid(String orderid) {
    this.orderid = orderid;
  }

  public String getEquipment() {
    return equipment;
  }

  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }

  public String getFalultDesc() {
    return falultDesc;
  }

  public void setFalultDesc(String falultDesc) {
    this.falultDesc = falultDesc;
  }

  public String getRepairTime() {
    return repairTime;
  }

  public void setRepairTime(String repairTime) {
    this.repairTime = repairTime;
  }

  public String getRepairName() {
    return repairName;
  }

  public void setRepairName(String repairName) {
    this.repairName = repairName;
  }

  public String getBookingTime() {
    return bookingTime;
  }

  public void setBookingTime(String bookingTime) {
    this.bookingTime = bookingTime;
  }

  public String getEquipClass() {
    return "1".equals(equipClass) ? "诊断设备" : "治疗设备";
  }

  public void setEquipClass(String equipClass) {
    this.equipClass = equipClass;
  }

  public String getEquipId() {
    return equipId;
  }

  public void setEquipId(String equipId) {
    this.equipId = equipId;
  }

  public String getEquipType() {
    return equipType;
  }

  public void setEquipType(String equipType) {
    this.equipType = equipType;
  }

  public String getOperFirm() {
    return operFirm;
  }

  public void setOperFirm(String operFirm) {
    this.operFirm = operFirm;
  }

  public String getSaleFirm() {
    return saleFirm;
  }

  public void setSaleFirm(String saleFirm) {
    this.saleFirm = saleFirm;
  }

  public String getOperCyc() {
    return operCyc;
  }

  public void setOperCyc(String operCyc) {
    this.operCyc = operCyc;
  }

  public String getEquipBrand() {
    return equipBrand;
  }

  public void setEquipBrand(String equipBrand) {
    this.equipBrand = equipBrand;
  }

  public String getEngineerName() {
    return engineerName;
  }

  public void setEngineerName(String engineerName) {
    this.engineerName = engineerName;
  }

  public String getIntDate() {
    return intDate;
  }

  public void setIntDate(String intDate) {
    this.intDate = intDate;
  }

  public String getEquipName() {
    return equipName;
  }

  public void setEquipName(String equipName) {
    this.equipName = equipName;
  }

  public String getEquipSeq() {
    return equipSeq;
  }

  public void setEquipSeq(String equipSeq) {
    this.equipSeq = equipSeq;
  }

  public int getIsAction() {
    return isAction;
  }

  public void setIsAction(int isAction) {
    this.isAction = isAction;
  }

  public String getDepar() {
    return depar;
  }

  public void setDepar(String depar) {
    this.depar = depar;
  }

  public String getQc() {
    return qc;
  }

  public void setQc(String qc) {
    this.qc = qc;
  }

  public String getScaling() {
    return scaling;
  }

  public void setScaling(String scaling) {
    this.scaling = scaling;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getUseState() {
    return useState;
  }

  public void setUseState(int useState) {
    this.useState = useState;
  }

  public String getEngineerId() {
    return engineerId;
  }

  public void setEngineerId(String engineerId) {
    this.engineerId = engineerId;
  }

  public String getUpUseCyc() {
    return upUseCyc;
  }

  public void setUpUseCyc(String upUseCyc) {
    this.upUseCyc = upUseCyc;
  }

  public String getEquipAddress() {
    return equipAddress;
  }

  public void setEquipAddress(String equipAddress) {
    this.equipAddress = equipAddress;
  }

  public String getHospital() {
    return hospital;
  }

  public void setHospital(String hospital) {
    this.hospital = hospital;
  }

  public String getEquipBar() {
    return equipBar;
  }

  public void setEquipBar(String equipBar) {
    this.equipBar = equipBar;
  }

  public String getImgurl() {
    return imgurl;
  }

  public void setImgurl(String imgurl) {
    this.imgurl = imgurl;
  }
}
