#!/bin/bash

set -e

argc=$#
CURRENT_PATH=`dirname $0`
TOOLS_PATH=`readlink -ev $CURRENT_PATH`

RESULT_DIR="result-$1-$2"
RESULT_PATH=
IMAGE_TYPE=$2

MACHINE_NAME=$1
BOARD_SOCNAME=
BOARD_NAME=
BOARD_PREFIX=
BOARD_POSTFIX=

BUILD_ALL=$3

META_NEXELL_PATH=`readlink -ev ${TOOLS_PATH}/..`
BUILD_PATH=
TMP_DEPLOY_PATH=

declare -A KERNEL_BIN_NAME
KERNEL_BIN_NAME["s5p4418"]="zImage"
KERNEL_BIN_NAME["s5p6818"]="Image"

declare -A UBUNTU_IMAGE_LOCATION
UBUNTU_IMAGE_LOCATION["s5p4418"]="/s5p4418/ubuntu/rootfs/"
#UBUNTU_IMAGE_LOCATION["s5p6818"]="/s5p6818/ubuntu/rootfs/"
UBUNTU_IMAGE_LOCATION["s5p6818"]="/s5p4418/ubuntu/rootfs/"

NEXELL_RELEASE_SERVER_ADDRESS="http://192.168.1.25:8070"
UBUNTU_CONSOLE_VERSION_TRUSTY="ubuntu-rootfs-console-trusty.tar.gz"
UBUNTU_CONSOLE_VERSION_XENIAL="ubuntu-rootfs-console-xenial.tar.gz"
UBUNTU_DESKTOP_VERSION_TRUSTY="ubuntu-rootfs-desktop.tar.gz"
UBUNTU_DESKTOP_LXDE_VERSION="ubuntu-rootfs-desktop-lxde.tar.gz"
UBUNTU_ROOTFS=${UBUNTU_CONSOLE_VERSION_TRUSTY}

UBUNTU_KSELFTESTS="kselftests.tar.gz"
UBUNTU_NX_TESTSUITE="testsuite.tar.gz"
UBUNTU_NX_LIBS="nxlibs.tar.gz"

function check_usage()
{
    if [ $argc -lt 3 ];then
	BUILD_ALL=true
    elif [ $argc -eq 3 ];then
        echo "Run at $PWD"
    else
        echo "Invalid argument check usage please"
        usage
        exit
    fi
}

function usage()
{
    echo "Usage: $0 <machine name> <image type>"
    echo "    ex) $0 s5p6818-avn-ref qt"
    echo "    ex) $0 s5p6818-avn-ref tiny"
    echo "    ex) $0 s5p4418-navi-ref qt"
    echo "    ex) $0 s5p4418-navi-ref tiny"
    echo "    ex) $0 s5p4418-navi-ref genivi"
}

function split_args()
{
    BOARD_SOCNAME=${MACHINE_NAME%-*-*}
    BOARD_NAME=${MACHINE_NAME#*-}
    BOARD_PREFIX=${BOARD_NAME%-*}
    BOARD_POSTFIX=${BOARD_NAME#*-}
}

function path_setup()
{
    BUILD_PATH=`readlink -ev ${META_NEXELL_PATH}/../../../build/build-${MACHINE_NAME}-${IMAGE_TYPE}`
    RESULT_PATH=${BUILD_PATH}/../../out/${RESULT_DIR}
    TMP_DEPLOY_PATH=${BUILD_PATH}/tmp/deploy/images/${MACHINE_NAME}
}

function cleanup_dirs()
{
    if [ ! -d ${RESULT_PATH} ];then
	    mkdir -p ${RESULT_PATH}
        chmod 777 ${RESULT_PATH}
        mkdir -p ${RESULT_PATH}/tools
        chmod 777 ${RESULT_PATH}/tools
    else
	rm -rf ${RESULT_PATH}/boot
	rm -rf ${RESULT_PATH}/root
    fi
}

function copy_bin_files()
{
    echo -e "\033[40;33m  >>>>   copy_bin_files        \033[0m"
    if [ "${USE_CONVERT_IMAGE_SCRIPT}" == "true" ]; then
        if [ "${BOARD_SOCNAME}" == "s5p6818" ]; then
            if [ "${BOARD_NAME}" == "artik710-raptor" ]; then
            cp ${TMP_DEPLOY_PATH}/bl1-raptor.bin ${RESULT_PATH}
            elif [ "${BOARD_NAME}" == "avn-ref" ]; then
                cp ${TMP_DEPLOY_PATH}/bl1-avn.bin ${RESULT_PATH}
            elif [ "${BOARD_NAME}" == "svt-ref" ]; then
                cp ${TMP_DEPLOY_PATH}/bl1-svt.bin ${RESULT_PATH}
            elif [ "${BOARD_NAME}" == "convergence-svmc" ]; then
                cp ${TMP_DEPLOY_PATH}/bl1-convergence.bin ${RESULT_PATH}
            fi
            cp ${TMP_DEPLOY_PATH}/fip-loader.bin ${RESULT_PATH}
            cp ${TMP_DEPLOY_PATH}/fip-nonsecure.bin ${RESULT_PATH}
            cp ${TMP_DEPLOY_PATH}/fip-secure.bin ${RESULT_PATH}
        else
            if [ "${BOARD_NAME}" == "zh-dragon" ]; then
                cp ${TMP_DEPLOY_PATH}/bl1-zh_dragon.bin ${RESULT_PATH}
            elif [ "${IMAGE_TYPE}" == "smartvoice" -o "${IMAGE_TYPE}" == "smartvoiceui" ]; then
                cp ${TMP_DEPLOY_PATH}/bl1-${BOARD_PREFIX}_voice.bin ${RESULT_PATH}
            else
                cp ${TMP_DEPLOY_PATH}/bl1-${BOARD_PREFIX}.bin ${RESULT_PATH}
            fi
            cp ${TMP_DEPLOY_PATH}/armv7_dispatcher.bin ${RESULT_PATH}
            cp ${TMP_DEPLOY_PATH}/pyrope-bl2.bin ${RESULT_PATH}
        fi
        cp ${TMP_DEPLOY_PATH}/bl1-emmcboot.bin ${RESULT_PATH}
        cp ${TMP_DEPLOY_PATH}/u-boot.bin ${RESULT_PATH}
        cp ${TMP_DEPLOY_PATH}/default_envs.txt ${RESULT_PATH}
    else
        cp -af ${TMP_DEPLOY_PATH}/bl1-*.bin ${RESULT_PATH}
        cp -af ${TMP_DEPLOY_PATH}/params.bin ${RESULT_PATH}
    fi
}

function copy_img_files()
{
    echo -e "\033[40;33m  >>>>   copy_img_files        \033[0m"
    cp -af ${TMP_DEPLOY_PATH}/*.img ${RESULT_PATH}
}

function copy_kernel_image()
{
    echo -e "\033[40;33m  >>>>   copy_kernel_image        \033[0m"

    rm -rf ${RESULT_PATH}/boot
    rm -rf ${RESULT_PATH}/boot.img

    cp ${TMP_DEPLOY_PATH}/${KERNEL_BIN_NAME[${BOARD_SOCNAME}]} ${RESULT_PATH}/

    if [ "${IMAGE_TYPE}" == "ubuntu" ];then
        if [ -d ${BUILD_PATH}/tmp/work/linux-kernel-selftests ]; then
            cp -a ${BUILD_PATH}/tmp/work/selftests ${BUILD_PATH}/tmp/work/extra-rootfs-support/usr/bin/
        fi
    fi
}

function copy_dtb_file()
{
    echo -e "\033[40;33m  >>>>   copy_dtb_file            \033[0m"
    #local deployed_dtb_file_name=${KERNEL_BIN_NAME[${BOARD_SOCNAME}]}-${BOARD_SOCNAME}*.dtb

    rm -rf ${RESULT_PATH}/*.dtb

    cp -af ${TMP_DEPLOY_PATH}/boot/*.dtb ${RESULT_PATH}

    # for i in `ls ${TMP_DEPLOY_PATH}/$deployed_dtb_file_name`
    # do
    #    filenameOnly="${i##*/}"
    #    dtbName=${filenameOnly#*-}
    #    echo -e "\033[40;33m  dtbName=$dtbName            \033[0m"
    #    echo -e "\033[40;33m  i=$i            \033[0m"
    #    cp $i ${RESULT_PATH}/$dtbName
    # done
}

function copy_ramdisk_image()
{
    echo -e "\033[40;33m  >>>>   copy_ramdisk_image       \033[0m"
    # temporary
    if [ "${BOARD_NAME}" == "ff-voice" -o "${IMAGE_TYPE}" == "ubuntu" ]; then
        cp ${TMP_DEPLOY_PATH}/"core-image-tiny-initramfs-${MACHINE_NAME}.cpio.gz" ${RESULT_PATH}
    else
        cp ${TMP_DEPLOY_PATH}/"core-image-minimal-initramfs-${MACHINE_NAME}.cpio.gz" ${RESULT_PATH}
    fi
}

function copy_rootfs_image()
{
    echo -e "\033[40;33m  >>>>   copy_rootfs_image        \033[0m"

    if [ "${IMAGE_TYPE}" == "ubuntu" ];then
        rm -rf ${RESULT_PATH}/*.ext4
        rm -rf ${RESULT_PATH}/rootfs.img

        echo -e "\033[40;33m  >>>>   download ubuntu image        \033[0m"
        wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_ROOTFS} -P ${RESULT_PATH}
        mv ${RESULT_PATH}/${UBUNTU_ROOTFS} ${RESULT_PATH}/rootfs.tar.gz

        echo -e "\033[40;33m  >>>>   copy_extra-rootfs-support to result dir        \033[0m"
        sudo cp -a ${BUILD_PATH}/tmp/work/extra-rootfs-support ${RESULT_PATH}

        # s5p6818 binary use armhf version, so kselftest used armhf version too.
        # kselftest do not build. using prebuilt binary
        if [ "${BOARD_SOCNAME}" == "s5p6818" ]; then
            echo -e "\033[40;33m  >>>>   extract kselftests        \033[0m"
            sudo rm -rf ${RESULT_PATH}/extra-rootfs-support/usr/bin/kselftests
            sudo rm -rf ${RESULT_PATH}/kselftests.*
            wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_KSELFTESTS} -P ${RESULT_PATH}
            sudo tar --overwrite -xvzf ${RESULT_PATH}/kselftests.tar.gz -C ${RESULT_PATH}/extra-rootfs-support/usr/bin/

            echo -e "\033[40;33m  >>>>   extract testsuites        \033[0m"
            sudo rm -rf ${RESULT_PATH}/testsuite.*
            wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_NX_TESTSUITE} -P ${RESULT_PATH}
            sudo tar --overwrite -xvzf ${RESULT_PATH}/testsuite.tar.gz -C ${RESULT_PATH}/extra-rootfs-support/usr/

            echo -e "\033[40;33m  >>>>   extract nexell libraries   \033[0m"
            sudo rm -rf ${RESULT_PATH}/nxlibs.*
            wget ${NEXELL_RELEASE_SERVER_ADDRESS}${UBUNTU_IMAGE_LOCATION[${BOARD_SOCNAME}]}${UBUNTU_NX_LIBS} -P ${RESULT_PATH}
            sudo tar --overwrite -xvzf ${RESULT_PATH}/nxlibs.tar.gz -C ${RESULT_PATH}/extra-rootfs-support/usr/
        fi
    else
        cp ${TMP_DEPLOY_PATH}/"nexell-${IMAGE_TYPE}-${MACHINE_NAME}.tar.bz2" ${RESULT_PATH}
        cp ${TMP_DEPLOY_PATH}/"nexell-${IMAGE_TYPE}-${MACHINE_NAME}.ext4" ${RESULT_PATH}
    fi
    cp ${META_NEXELL_PATH}/tools/fusing_tools/partition.txt ${RESULT_PATH}
}

function copy_partmap_file()
{
    if [ "${IMAGE_TYPE}" == "ubuntu" ]; then
        cp ${META_NEXELL_PATH}/tools/configs/board_partmap/partmap_emmc_${MACHINE_NAME}-ubuntu.txt ${RESULT_PATH}/partmap_emmc.txt
    else
        cp ${META_NEXELL_PATH}/tools/configs/board_partmap/partmap_emmc_${MACHINE_NAME}.txt ${RESULT_PATH}/partmap_emmc.txt
    fi
}

function post_process()
{
    if [ -f secure.cfg ]; then
	cp secure.cfg ${RESULT_PATH}
        echo -e "\n secure.cfg file copy Done!"
    fi

    touch ${RESULT_PATH}/YOCTO.${RESULT_DIR}.INFO.DoNotChange
    echo -e "\033[40;33m  Maybe you need to convert some binary images                                \033[0m"
    echo -e "\033[40;33m  You can use below operation                                                 \033[0m"
    echo -e "\033[40;33m  ex) ${META_NEXELL_PATH}/tools/convert_tools/convert_images.sh ${MACHINE_NAME} ${IMAGE_TYPE}  \033[0m\n"

    cp ${META_NEXELL_PATH}/tools/convert_tools/nsihbingen.py ${RESULT_PATH}
    cp ${META_NEXELL_PATH}/tools/convert_tools/nsihtxtmod.py ${RESULT_PATH}
    cp ${META_NEXELL_PATH}/tools/convert_tools/nsih-dummy.txt ${RESULT_PATH}
}

function copy_partition_files()
{
    if [ "${IMAGE_TYPE}" == "ubuntu" ]; then
        cp ${META_NEXELL_PATH}/tools/configs/board_partmap/partmap_emmc_${MACHINE_NAME}-ubuntu.txt ${RESULT_PATH}/tools/partmap_emmc.txt
    else
        cp ${META_NEXELL_PATH}/tools/configs/board_partmap/partmap_emmc_${MACHINE_NAME}.txt ${RESULT_PATH}/tools/partmap_emmc.txt
    fi

    cp -af ${TMP_DEPLOY_PATH}/partition.txt ${RESULT_PATH}/tools

}

function copy_tools_files() {
    cp -af ${META_NEXELL_PATH}/tools/fusing_tools/standalone-fastboot-download.sh ${RESULT_PATH}/tools/
    cp -af ${META_NEXELL_PATH}/tools/fusing_tools/standalone-uboot-by-usb-download.sh ${RESULT_PATH}/tools/
    cp -af ${META_NEXELL_PATH}/tools/fusing_tools/usb-downloader ${RESULT_PATH}/tools/
}

check_usage
split_args
path_setup
cleanup_dirs
copy_bin_files
if [ "${USE_CONVERT_IMAGE_SCRIPT}" == "true" ]; then
copy_kernel_image
copy_dtb_file
copy_ramdisk_image
if [ ${BUILD_ALL} == "true" ];then
    copy_rootfs_image
fi
copy_partmap_file
post_process
else
copy_img_files
copy_partition_files
copy_tools_files
touch ${RESULT_PATH}/YOCTO.${RESULT_DIR}.INFO.DoNotChange
fi
