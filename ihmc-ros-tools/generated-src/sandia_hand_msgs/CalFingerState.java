package sandia_hand_msgs;

public interface CalFingerState extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "sandia_hand_msgs/CalFingerState";
  static final java.lang.String _DEFINITION = "float64 fmcb_time\nfloat64 pp_time\nfloat64 dp_time\nfloat32[6]  pp_tactile\nfloat32[12] dp_tactile\nfloat32 pp_strain\nfloat32[3] mm_accel\nfloat32[3] pp_accel\nfloat32[3] dp_accel\nfloat32[3] mm_mag\nfloat32[3] pp_mag\nfloat32[3] dp_mag\nfloat32[4] pp_temp\nfloat32[4] dp_temp\nfloat32[3] fmcb_temp\nfloat32 fmcb_voltage\nfloat32 fmcb_pb_current\nint32[3] hall_tgt\nint32[3] hall_pos\nint16[3] fmcb_effort\nfloat32[3] joints_hall\nfloat32[3] joints_inertial\nfloat32[3] joints_inertial_variance\n";
  double getFmcbTime();
  void setFmcbTime(double value);
  double getPpTime();
  void setPpTime(double value);
  double getDpTime();
  void setDpTime(double value);
  float[] getPpTactile();
  void setPpTactile(float[] value);
  float[] getDpTactile();
  void setDpTactile(float[] value);
  float getPpStrain();
  void setPpStrain(float value);
  float[] getMmAccel();
  void setMmAccel(float[] value);
  float[] getPpAccel();
  void setPpAccel(float[] value);
  float[] getDpAccel();
  void setDpAccel(float[] value);
  float[] getMmMag();
  void setMmMag(float[] value);
  float[] getPpMag();
  void setPpMag(float[] value);
  float[] getDpMag();
  void setDpMag(float[] value);
  float[] getPpTemp();
  void setPpTemp(float[] value);
  float[] getDpTemp();
  void setDpTemp(float[] value);
  float[] getFmcbTemp();
  void setFmcbTemp(float[] value);
  float getFmcbVoltage();
  void setFmcbVoltage(float value);
  float getFmcbPbCurrent();
  void setFmcbPbCurrent(float value);
  int[] getHallTgt();
  void setHallTgt(int[] value);
  int[] getHallPos();
  void setHallPos(int[] value);
  short[] getFmcbEffort();
  void setFmcbEffort(short[] value);
  float[] getJointsHall();
  void setJointsHall(float[] value);
  float[] getJointsInertial();
  void setJointsInertial(float[] value);
  float[] getJointsInertialVariance();
  void setJointsInertialVariance(float[] value);
}
