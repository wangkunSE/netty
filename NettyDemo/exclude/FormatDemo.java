package com.soul.wk.exclude;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import car.w.k.domain.CarDetail;
import car.w.k.domain.CarGeneral;
import car.w.k.mapper.CarDetailMapper;
import car.w.k.mapper.CarGeneralMapper;
import car.w.k.service.ICarGeneralService;
import car.w.k.test.TestDemo;

public class CarSocketServer implements Runnable {

    private static Logger logger = Logger.getLogger(CarSocketServer.class);
    @Resource
    private CarDetailMapper carDetailMapper;

    @Resource
    private CarGeneralMapper carGeneralMapper;

    private final int port = 12345;
    private ServerSocket serverSocket;


    public CarSocketServer() {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("================================服务器启动在：" + port + "端口");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void socketDaemon() {
        Thread thread = new Thread(this);
        thread.start();

    }

    @Override
    public void run() {
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept(); // 从连接请求队列中取出一个连接
                // 处理该socket
                try {
                    Thread.sleep(100);// 等待时间
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (socket != null) {
                    socket.setKeepAlive(true);
                    new HandlerThread(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class HandlerThread implements Runnable {

        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        /*public void run() {


             * InputStream input = null; try { //读取客户端数据
             * System.out.println(socket.getInetAddress()+":"+socket.getPort());
             * input = socket.getInputStream(); byte[] bytes = null; bytes = new
             * byte[input.available()]; input.read(bytes); //手机号
             * if(bytes.length<30){ System.out.println("数据少于30位！"); //有用数据
             * }else{ charge(bytes); }


            InputStream input = null;
            try {

                System.out.println(socket.getInetAddress() + ":" + socket.getPort());
                input = socket.getInputStream();


                 * int len = 0; byte[] bytes = new byte[100]; while(len < 100 ){
                 * len += input.read(bytes, len, 100-len); }

                int len = 0;
                byte[] bytes = new byte[52];
                int count = 2;
                while ((len = input.read(bytes)) != -1 && count > 0)
                    count--;

                 * while(len < 100 ){ len += input.read(bytes, len, 100-len); }

                String str = new String(bytes, 0, 21);

                int start = str.indexOf("1");
                String substring = null;
                if( !(21-start < 11)){
                    substring = str.substring(start, start+11);
                }
//				System.out.println(str);
                System.out.println(substring);

                int index = 52;
                for (int i = 0; i < 52; i++) {
                    if (bytes[i] == (byte) 0xAA && bytes[i + 1] == 0x55) {
                        index = i;
                        break;
                    }
                }
                byte[] data = new byte[31];

                if (52 - index < 31) {

                    System.out.println("非法数据！！");

                } else {
                    for (int i = 0, j = index; i < 31; i++, j++) {
                        data[i] = bytes[j];
                    }
                    charge(data);
                }

            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
                e.printStackTrace();
            } finally {

                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        if (!socket.isClosed())
                            socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
*/
        //连接处理函数
        public void run() {


            CarGeneral carGeneral = null;
            InputStream input = null;
            String phoneNumber = null;

            try {

                System.out.println(socket.getInetAddress() + ":" + socket.getPort());
                input = socket.getInputStream();

				/*
                 * int len = 0; byte[] bytes = new byte[100]; while(len < 100 ){
				 * len += input.read(bytes, len, 100-len); }
				 */
                int len = 0;
                byte[] bytes = new byte[56];
                byte[] phonenumber = new byte[11];
                byte[] data = new byte[35];
                //10s没有任何信息断开连接
                socket.setSoTimeout(10000);

                while ((len = input.read(bytes)) != -1) {

                    if (len <= 2) {
                        System.out.printf("%s%x\n", "HeartBeatPackage:0x", (0xff & bytes[0]));
                    } else {

                        copyBytesArray(bytes, phonenumber, 4, 0);

                        String tempPhone = getPhoneNumber(phonenumber);

                        if (tempPhone != null) {

                            phoneNumber = tempPhone;
                            //TODO 设置当前手机号状态为在线
                            carGeneral = online(phoneNumber);

                            if (carGeneral == null)
                                System.out.println("更新状态失败！");

                        }
                        System.out.println(phoneNumber);

                        //获取实际数据的索引
                        int index = getIndex(bytes);


                        if (56 - index < 35) {

                            System.out.println("非法数据！！");

                        } else {
                            //将有效数据拷贝到data数组中
                            copyBytesArray(bytes, data, index, 0);
							/*for (int i = 0, j = index; i < 31; i++, j++) {
							data[i] = bytes[j];
						}*/
                            charge(data, phoneNumber, carGeneral);

                        }//else

                        for (int i = 0; i < bytes.length; i++) {
                            bytes[i] = 0;
                        }
                    }


                }//while

            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {

                //TODO 设置当前手机号为离线状态
                if (phoneNumber != null) {

                    carGeneral = offline(phoneNumber);
                    if (carGeneral == null)
                        System.out.println("更新状态失败！");
                    phoneNumber = null;
                }

                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        if (!socket.isClosed())
                            socket.close();
                    } catch (Exception e) {

                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

        }


        private String getPhoneNumber(byte[] phonenumber) {
            String str = new String(phonenumber, 0, 11);
            int start = str.indexOf("1");
            if (start != 0 || str.length() < 11) {
                return null;
            } else {

                return str;
            }
        }

        private void copyBytesArray(byte[] src, byte[] target, int srcIndex, int tarIndex) {

            for (int i = srcIndex, j = tarIndex; i < src.length && j < target.length; i++, j++) {
                target[j] = src[i];
            }
        }

        private int getIndex(byte[] bytes) {

            int index = bytes.length;
            for (int i = 0; i < 30; i++) {
                if (bytes[i] == (byte) 0xAA && bytes[i + 1] == 0x55) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        private void charge(byte[] bytes, String phoneNumber, CarGeneral carGeneral) throws IOException {

            int[] ints = new int[35];
            for (int i = 0; i < 35; i++) {
                ints[i] = 0xff & bytes[i];
            }

            int count = 0;
            for (Integer integer : ints) {
                System.out.printf("%x\t", integer);
                count++;
                if ((count % 9) == 0)
                    System.out.println();
            }
            int year = ints[4] + 2000;
            int month = ints[5];
            int day = ints[6];
            int hour = ints[7];
            int minute = ints[8];
            int second = ints[9];
            String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

			/*
			 * byte[] fnumberByte = new byte[2]; copyByteArray(fnumberByte,
			 * bytes, 10, 11); int fnumber = ByteArrayToInt(fnumberByte);
			 *
			 * // 井号前3位
			 */
            int fnumber = byte2int(bytes, 10);

			/*
			 * byte[] bnumberByte = new byte[2]; copyByteArray(bnumberByte,
			 * bytes, 12, 13);
			 */
            // int bnumber = ByteArrayToInt(bnumberByte);

            // 井号后3位
            int bnumber = byte2int(bytes, 12);

			/*
			 * byte[] depthByte = new byte[4]; copyByteArray(depthByte, bytes,
			 * 14, 17);
			 */

            // 当前深度
            long depth = byte2long(bytes, 14);

			/*
			 * byte[] velocityByte = new byte[4]; copyByteArray(velocityByte,
			 * bytes, 18, 21);
			 */
            // 当前速度
            long velocity = byte2long(bytes, 18);

			/*
			 * // (0xff00&(bytes[24]<<8))+((bytes[23] << 24) >>> 8)+ints[25]
			 * byte[] tensionByte = new byte[4]; copyByteArray(tensionByte,
			 * bytes, 22, 25);
			 */

            // 张力
            long tension = byte2long(bytes, 22);
            // byte[] pulseByte = new byte[2];
            // copyByteArray(pulseByte, bytes, 26, 27);
            // int pulse = ByteArrayToInt(pulseByte);
//			System.out.println("===========tension:"+bytes[22]+bytes[23]+bytes[24]+bytes[25]+" : "+tension);

            //差分张力
            long tensionex = byte2long(bytes, 26);
//			System.out.println("===========tensionex:"+bytes[26]+bytes[27]+bytes[28]+bytes[29]+" : "+tensionex);
            // 脉冲
            int pulse = byte2int(bytes, 30);

			/*
			 * byte[] kValueByte = new byte[2]; copyByteArray(kValueByte, bytes,
			 * 28, 29); int kValue = ByteArrayToInt(kValueByte);
			 */
            // K值
            int kValue = byte2int(bytes, 32);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CarDetail carDetail = new CarDetail();
            carDetail.setPhoneNumber(phoneNumber);
            carDetail.setDepth(depth);
            carDetail.setNumber(fnumber + "--" + bnumber);
            carDetail.setKvalue(kValue);
            carDetail.setPulse(pulse);
            carDetail.setInserttime(new Date());
            carDetail.setTension(tension);
            carDetail.setTensionex(tensionex);
            carDetail.setVelocity(velocity);
            try {
                Date date = sdf.parse(time);
                carDetail.setReceivetime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //分表测试
            carDetail.setTeamId(carGeneral.getTid());
            // 入库
            carDetailMapper.insertWithTeamID(carDetail);
//			carDetailMapper.insert(carDetail);
            System.out.println("插入成功！！");
            System.out.println(carDetail);
            System.out.println("--------------------------------");
        }


        private int byte2long(byte[] bytes, int offsets) {

            int targets = (int) ((((0xff & bytes[offsets])) << 24) | (((0xff & bytes[offsets + 1])) << 16)
                    | (((0xff & bytes[offsets + 2])) << 8) | (((0xff & bytes[offsets + 3]))));
            return targets;
        }

        private int byte2int(byte[] bytes, int offsets) {

            int targets = (((int) (0xff & bytes[offsets])) << 8) + (((int) (0xff & bytes[offsets + 1])));
            return targets;
        }

        public CarGeneral updateStatus(String phoneNumber) {

            CarGeneral carGeneral = carGeneralMapper.selectByPrimaryKey(phoneNumber);

            if (carGeneral != null) {

                if (carGeneral.getFlag() == 0)
                    carGeneral.setFlag(1);
                else {
                    carGeneral.setFlag(0);
                }
                carGeneralMapper.updateByPrimaryKey(carGeneral);
            }
            return carGeneral;
        }
    }

    private CarGeneral online(String phoneNumber) {

        CarGeneral carGeneral = carGeneralMapper.selectByPrimaryKey(phoneNumber);

        if (carGeneral != null) {

            if (carGeneral.getFlag() == 1) {
                return carGeneral;
            }

            carGeneral.setFlag(1);
            carGeneralMapper.updateByPrimaryKey(carGeneral);

        }

        return carGeneral;
    }

    private CarGeneral offline(String phoneNumber) {

        CarGeneral carGeneral = carGeneralMapper.selectByPrimaryKey(phoneNumber);
        if (carGeneral != null) {

            if (carGeneral.getFlag() == 0) {
                return carGeneral;
            }

            carGeneral.setFlag(0);
            carGeneralMapper.updateByPrimaryKey(carGeneral);

        }

        return carGeneral;
    }

}
