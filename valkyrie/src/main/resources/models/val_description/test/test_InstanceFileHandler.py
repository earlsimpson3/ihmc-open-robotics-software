#!/usr/bin/env python

import unittest
import os
from val_description.InstanceFileHandler import InstanceFileHandler
import xml.etree.ElementTree as xmlParser


class instanceFileHandlerTests(unittest.TestCase):

    def setUp(self):
        self.testDirectory = os.path.dirname(os.path.abspath(__file__))

    def tearDown(self):
        pass

    def testGetXmlRoot(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        assert instanceFileHandler.getInstanceRoot().tag == 'robot'

    def testGetActuatorCoeffFromNodeName(self):
        sampleInstanceFile = self.testDirectory + '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        keys = ['/left_leg/j1', '/left_leg/ankle/actuator0', '/left_leg/ankle/actuator1']
        testFiles = ['test_v_a_001.xml', 'test_v_e_001.xml', 'test_v_e_002.xml']

        assert instanceFileHandler.getActuatorCoeffFileByNode(keys[0]) == testFiles[0]
        assert instanceFileHandler.getActuatorCoeffFileByNode(keys[1]) == testFiles[1]
        assert instanceFileHandler.getActuatorCoeffFileByNode(keys[2]) == testFiles[2]

    def testGetMechanisms(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        mechanisms = instanceFileHandler.getMechanisms()
        mechanism_ids = ['leftHipYaw',
                         'leftHipRoll',
                         'leftHipPitch',
                         'leftKneePitch',
                         'leftAnkle',
                         'rightHipYaw',
                         'rightHipRoll',
                         'rightHipPitch',
                         'rightKneePitch',
                         'rightAnkle',
                         'leftShoulderPitch',
                         'leftShoulderRoll',
                         'leftShoulderYaw',
                         'leftElbowPitch',
                         'leftForearmYaw',
                         'leftWrist',
                         'rightShoulderPitch',
                         'rightShoulderRoll',
                         'rightShoulderYaw',
                         'rightElbowPitch',
                         'rightForearmYaw',
                         'rightWrist',
                         'lowerNeckPitch',
                         'neckYaw',
                         'upperNeckPitch',
                         'torso_yaw',
                         'waist']

        for mechanism in mechanisms:
            assert mechanism.tag == 'Mechanism'
            assert mechanism.get('id') in mechanism_ids

    def testGetChannels(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        channels = instanceFileHandler.getChannels()
        channelsToCheck = [
            '/right_arm', '/left_arm', '/right_leg', '/left_leg', '/neck', '/trunk']
        for channel in channels:
            assert channel.tag == 'Channel'
            assert channel.get('id') in channelsToCheck

    def testGetDevices(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        devices = instanceFileHandler.getDevices()
        devicesToCheck = ['pelvis_middle_imu', 'pelvis_rear_imu', 'torso_left_imu',
                          'torso_right_imu', 'left_foot_force_torque', 'right_foot_force_torque']
        for device in devices:
            assert device.tag == 'Device'
            assert device.get('id') in devicesToCheck

    def testGetActuatorSerialNumbers(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        serialNumbers = instanceFileHandler.getSerialNumbers()
        serialNumbersToCheck = ['v_a_006',
                                'v_b_002',
                                'v_c_002',
                                'v_d_001',
                                'v_e_001',
                                'v_e_002',
                                'v_a_003',
                                'v_b_001',
                                'v_c_001',
                                'v_d_004',
                                'v_e_003',
                                'v_e_004',
                                'v_a_005',
                                'v_b_004',
                                'v_f_005',
                                'v_f_002',
                                'v_g_005',
                                'UNKNOWN',
                                'UNKNOWN',
                                'v_a_001',
                                'v_b_003',
                                'v_f_004',
                                'v_f_003',
                                'v_g_003',
                                'UNKNOWN',
                                'UNKNOWN',
                                'v_g_006',
                                'v_g_006',
                                'v_g_006',
                                'v_a_004',
                                'v_e_006',
                                'v_e_005']

        for serialNumber in serialNumbers:
            assert serialNumber in serialNumbersToCheck

    def testGetActuatorCoeffFiles(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        coeffFiles = instanceFileHandler.getActuatorCoeffFiles()
        coeffFilesToCheck = ['v_a_006.xml',
                             'v_b_002.xml',
                             'v_c_002.xml',
                             'v_d_001.xml',
                             'v_e_001.xml',
                             'v_e_002.xml',
                             'v_a_003.xml',
                             'v_b_001.xml',
                             'v_c_001.xml',
                             'v_d_004.xml',
                             'v_e_003.xml',
                             'v_e_004.xml',
                             'v_a_005.xml',
                             'v_b_004.xml',
                             'v_f_005.xml',
                             'v_f_002.xml',
                             'v_g_005.xml',
                             'UNKNOWN.xml',
                             'UNKNOWN.xml',
                             'v_a_001.xml',
                             'v_b_003.xml',
                             'v_f_004.xml',
                             'v_f_003.xml',
                             'v_g_003.xml',
                             'UNKNOWN.xml',
                             'UNKNOWN.xml',
                             'v_g_006.xml',
                             'v_g_006.xml',
                             'v_g_006.xml',
                             'v_a_004.xml',
                             'v_e_006.xml',
                             'v_e_005.xml']
        for coeffFile in coeffFiles:
            assert coeffFile in coeffFilesToCheck

    def testGetActuatorSerialNumberByNode(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        node = '/left_leg/j4'
        serialNumberToCheck = 'v_d_001'
        serialNumber = instanceFileHandler.getActuatorSerialNumberByNode(node)

        assert serialNumber == serialNumberToCheck

        node = '/left_leg/ankle/left_actuator'
        serialNumberToCheck = 'v_e_001'
        serialNumber = instanceFileHandler.getActuatorSerialNumberByNode(node)

        assert serialNumber == serialNumberToCheck

    def testGetNodes(self):
        sampleInstanceFile = self.testDirectory + '/test_files/valkyrie_A.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)
        nodes = instanceFileHandler.getNodeNames()
        nodesToCheck = ['/left_leg/j1',
                        '/left_leg/j2',
                        '/left_leg/j3',
                        '/left_leg/j4',
                        '/left_leg/ankle/left_actuator',
                        '/left_leg/ankle/right_actuator',
                        '/right_leg/j1',
                        '/right_leg/j2',
                        '/right_leg/j3',
                        '/right_leg/j4',
                        '/right_leg/ankle/left_actuator',
                        '/right_leg/ankle/right_actuator',
                        '/left_arm/j3',
                        '/left_arm/j2',
                        '/left_arm/j3',
                        '/left_arm/j4',
                        '/left_arm/j5',
                        '/left_arm/wrist/top_actautor',
                        '/left_arm/wrist/bottom_actuator',
                        '/right_arm/j3',
                        '/right_arm/j2',
                        '/right_arm/j3',
                        '/right_arm/j4',
                        '/right_arm/j5',
                        '/right_arm/wrist/top_actautor',
                        '/right_arm/wrist/bottom_actuator',
                        '/neck/j1',
                        '/neck/j2',
                        '/neck/j3',
                        '/trunk/j1',
                        '/trunk/waist/left_actuator',
                        '/trunk/waist/right_actuator']

        for node in nodes:
            assert node in nodesToCheck

    def testInstanceConfigDictionary(self):
        sampleInstanceFile = self.testDirectory + \
            '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        instanceConfig = instanceFileHandler.getInstanceConfig()

        # Check some of them, to many to check them all though.
        assert('test_v_e_001.xml' in instanceConfig['/left_leg/ankle/actuator0']['configFiles'])
        assert('test_e_futek_dh.xml' in instanceConfig['/left_leg/ankle/actuator0']['configFiles'])
        assert('test_ankle_linear.xml' in instanceConfig['/left_leg/ankle/actuator0']['configFiles'])

        assert('test_v_e_003.xml' in instanceConfig['/trunk/left_actuator']['configFiles'])
        assert('test_e_renishaw_dh.xml' in instanceConfig['/trunk/left_actuator']['configFiles'])
        assert('test_trunk_linear.xml' in instanceConfig['/trunk/left_actuator']['configFiles'])

        assert('test_v_a_001.xml' in instanceConfig['/left_leg/j1']['configFiles'])

    def testGetFirmwareType(self):
        sampleInstanceFile = self.testDirectory + \
            '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        expectedFirmwareType = 'rotary/turbo_bootloader.bin'
        firmwareType = instanceFileHandler.getFirmware('/left_leg/j1')

        assert firmwareType == expectedFirmwareType

    def testGetNodeType(self):
        sampleInstanceFile = self.testDirectory + \
            '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        expectedNodeType = 'turbodriver'
        nodeType = instanceFileHandler.getNodeType('/left_leg/j1')

        assert nodeType == expectedNodeType

    def testGatherCoeffs(self):
        sampleInstanceFile = self.testDirectory + \
            '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        coeffs = instanceFileHandler.gatherCoeffs("/left_leg/j1")

        expectedCoeffs = {'TemperatureSensor_SensorLoc2': 2.0, 'TemperatureSensor_SensorLoc1': 1.0, 'JointSensors_OutputPosition': 2.0,
                          'IGainAmpsPerBit': 0.018928, 'DeltaAPSSafeLimit': 9999.0, 'TorqueControl_enablePID': 1.0,
                          'TorqueControl_FFd_fc_Hz': 25.0, 'APS1DriftSafeLimit': 9999.0, 'PositionControl_MotorTorqueDirection': 1.0,
                          'TorqueControl_Kd_fc_Hz': 50.0, 'VelocitySafeLimit': 9999.0, 'WindingResistance': 2.112, 'JointOutputAPS_MountingGain': 1.0,
                          'JointSafety_LowerLimit_Rad': -3.15, 'CommTimeoutMs': 80.0, 'PhaseACurOffset': 2048.0, 'JerkSafeLimit': 9999.0,
                          'BusVoltage_SensorGain': 0.163412, 'PositionOffset_Rad': -1.6651, 'EncMountingDir': 1.0, 'TorqueControl_TdobWindupLimit_Nm': 80.0,
                          'MotorAccFilter_fc_Hz': 50.0, 'JointOutputAPS_CountsToRad': 0.00076699038, 'PhaseCCurOffset': 2045.0, 'SpaceVector_MaxNormVoltage': 0.666,
                          'MotorWindingType': 0.0, 'TorqueControl_Tdob_fc_Hz': 50.0, 'JointSensors_OutputVelocity': 1.0, 'Renishaw_CountsToRad': 5.8516723e-09,
                          'TorqueControl_m': 1.2, 'EncoderIndexOffset': 1.16973095726, 'BusVoltage_BitOffset': 2048.0, 'SpringStiffness': 2750.0,
                          'Inductance_DAxis': 0.0009, 'MotorVelFilter_fc_Hz': 800.0, 'JointKinematicDir': -1.0, 'TorqueOffset_Nm': -9.39, 'TemperatureSensor_MaxTemp1': 125.0,
                          'TorqueControl_MotorTorqueDirection': 1.0, 'TemperatureSensor_MaxTemp2': 110.0, 'PositionControl_Kd': 1.0, 'TorqueControl_Current2MotorTorque': 0.0375,
                          'PhaseBCurOffset': 2048.0, 'TorqueControl_PD_damp': 0.95, 'EncDriftSafeLimit': 9999.0, 'DeadTimeCompensation': 0.02, 'TorqueControl_b': 70.0, 'TorqueControl_enableDOB': 0.0,
                          'SpringAPS_MountingGain': -1.0, 'PositionControl_Kd_fc_Hz': 50.0, 'Inductance_QAxis': 0.00139, 'JointVelFilter_fc_Hz': 30.0,
                          'PositionControl_Kp': 500.0, 'TorqueControl_enableFF': 1.0, 'JointGearRatio': 160.0, 'NumberOfPoles': 8.0, 'PositionControl_SensorFeedback': 4.0,
                          'PositionControl_Input_fc_Hz': 30.0, 'JointMinValue': -3.14159265359, 'FluxLinkage': 0.0444, 'TorqueControl_Kd': 0.03, 'JointTorqueLimit_Nm': 10.0,
                          'TorqueControl_Kp': 3.351, 'SpringAPS_BitOffset': 115108200.0, 'JointSensors_MotorPosition': 1.0, 'JointSafety_LimitZone_Rad': 0.07,
                          'TorqueControl_enableDynFF': 0.0, 'TorqueControl_autoKd': 0.0, 'JointSensors_OutputForce': 2.0, 'PositionControl_enableInLPF': 1.0,
                          'Commutation_Select': 2.0, 'JointMaxValue': 3.14159265359, 'CurrVelFilter_fc_Hz': 200.0, 'TorqueControl_ParallelDamping': 0.0, 'JointSafety_UpperLimit_Rad': 3.15,
                          'EncoderCPR': 544.0, 'SpaceVector_CurrentToSV': 1.0, 'CurrentSafeLimit': 13.0, 'EffortControl_Alpha': 0.0, 'EffortControl_AlphaDot': 0.5}
        import difflib
        a = '\n'.join(['%s:%s' % (key, value) for (key, value) in sorted(coeffs.items())])
        b = '\n'.join(['%s:%s' % (key, value) for (key, value) in sorted(expectedCoeffs.items())])
        for coeff in coeffs:
            assert not coeff == None

    def testGatherCoeffsHandleKeyError(self):
        sampleInstanceFile = self.testDirectory + \
            '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

    # This next line should NOT raise a key error!
        coeffs = instanceFileHandler.gatherCoeffs("/bum_leg/j1")

    def testLoadXMLCoeffs(self):
        sampleInstanceFile = self.testDirectory + \
            '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        expectedCoeffs = {'JointSensors_OutputPosition': {'source': 'test_v_a_001.xml', 'value': 2.0},
                          'PositionControl_MotorTorqueDirection': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'JointOutputAPS_MountingGain': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'SpringAPS_MountingGain': {'source': 'test_v_a_001.xml', 'value': -1.0},
                          'PositionControl_enableInLPF': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'PositionOffset_Rad': {'source': 'test_v_a_001.xml', 'value': -1.6651},
                          'JointSensors_OutputVelocity': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'TorqueOffset_Nm': {'source': 'test_v_a_001.xml', 'value': -9.39},
                          'EncoderIndexOffset': {'source': 'test_v_a_001.xml', 'value': 1.16973095726},
                          'JointKinematicDir': {'source': 'test_v_a_001.xml', 'value': -1.0},
                          'TorqueControl_MotorTorqueDirection': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'EncMountingDir': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'JointMaxValue': {'source': 'test_v_a_001.xml', 'value': 3.14159265359},
                          'JointSensors_OutputForce': {'source': 'test_v_a_001.xml', 'value': 2.0},
                          'JointMinValue': {'source': 'test_v_a_001.xml', 'value': -3.14159265359},
                          'SpringAPS_BitOffset': {'source': 'test_v_a_001.xml', 'value': 115108200.0},
                          'JointSensors_MotorPosition': {'source': 'test_v_a_001.xml', 'value': 1.0},
                          'JointSafety_LimitZone_Rad': {'source': 'test_v_a_001.xml', 'value': 0.07},
                          'PositionControl_Input_fc_Hz': {'source': 'test_v_a_001.xml', 'value': 30.0}}

        assert cmp(instanceFileHandler.loadXMLCoeffs('test_v_a_001.xml'), expectedCoeffs) == 0

    def testLoadAnkleInstanceFile(self):
        sampleInstanceFile = self.testDirectory + '/test_files/ankle_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        nodesToCheck = instanceFileHandler.getNodeNames()

        assert '/left_leg/ankle/left_actuator' in nodesToCheck
        assert '/left_leg/ankle/right_actuator' in nodesToCheck

    def testForearmCoeffs(self):
        sampleInstanceFile = self.testDirectory + "/test_files/test_forearm.xml"
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        node1 = '/forearm/forearm_driver1'
        node2 = '/forearm/forearm_driver2'

        nodeNames = instanceFileHandler.getNodeNames()

        assert(node1 in nodeNames)
        assert(node2 in nodeNames)

        forearmCoeffDictionary = instanceFileHandler.getForearmCoeffDictionary()

        expectedAthenaDictionary = {}
        expectedAthenaDictionary[node1] = {'Pitch_offset': -2331, 'Yaw_offset': 1596, 'Pitch_hilmt': 1500, 'Pitch-lolmt': -1500, 'Pitch_cnvtrad': -0.000767}
        expectedAthenaDictionary[node2] = {'Fing1_elecoffset': 0.0, 'Fing1_locmdlmt': 100.0, 'Fing1_hicmdlmt': 5214.0, 'Fing1_posKP': 1.0, 'Fing1_posKD': 0.0}

        assert cmp(forearmCoeffDictionary[node1], expectedAthenaDictionary[node1])
        for key, value in forearmCoeffDictionary[node2].iteritems():
            assert(forearmCoeffDictionary[node2][key] - value == 0)

    def testSubclassFiles(self):
        sampleInstanceFile = self.testDirectory + '/test_files/sample_instance.xml'
        instanceFileHandler = InstanceFileHandler(sampleInstanceFile)

        ankleLinearCoeffs = instanceFileHandler.gatherCoeffs('/left_leg/ankle/actuator0')
        torsoLinearCoeffs = instanceFileHandler.gatherCoeffs('/trunk/left_actuator')

        assert(torsoLinearCoeffs['ForceControl_SensorFeedback'] == 0)
        assert(ankleLinearCoeffs['ForceControl_SensorFeedback'] == 1)

        assert(torsoLinearCoeffs['ForceControl_Kp'] == 1)
        assert(torsoLinearCoeffs['ForceControl_Kd'] == 2)

        assert(ankleLinearCoeffs['ForceControl_Kp'] == 3)
        assert(ankleLinearCoeffs['ForceControl_Kd'] == 4)

        assert(torsoLinearCoeffs['EffortControl_Alpha'] == 0.99)
        assert(torsoLinearCoeffs['EffortControl_AlphaDot'] == 0.9)

        assert(ankleLinearCoeffs['EffortControl_Alpha'] == 0.93)
        assert(ankleLinearCoeffs['EffortControl_AlphaDot'] == 0.45)

        assert(torsoLinearCoeffs['SpringStiffness'] == 714000.0)

        assert(ankleLinearCoeffs['SpringStiffness'] == 1.6085e6)


if __name__ == '__main__':
    unittest.main()
