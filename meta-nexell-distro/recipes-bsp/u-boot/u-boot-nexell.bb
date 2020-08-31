require u-boot-nexell.inc

DESCRIPTION = "U-Boot for NEXELL"

EXTRA_OEMAKE = 'ARCH=arm CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" V=1 QUICKBOOT=${QUICKBOOT_ENABLE}'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
DEPENDS = "dtc-native bc-native"

do_compile() {
    oe_runmake clean
    oe_runmake ${UBOOT_DEFCONFIG}
    oe_runmake
}

do_install() {
    cp `find . -name "env_common.o"` copy_env_common.o
    ${TARGET_PREFIX}objcopy -O binary --only-section=.rodata.default_environment `find . -name "copy_env_common.o"`
    tr '\0' '\n' < copy_env_common.o > default_envs.txt
    cp default_envs.txt default_envs.txt.orig
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/u-boot.bin ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/default_envs.txt ${DEPLOY_DIR_IMAGE}

    if [ ${NEXELL_BOARD_SOCNAME} != "s5p6818" ]; then
        ${NEXELL_SECURE_BINGEN} -c ${NEXELL_BOARD_SOCNAME} \
            -t 3rdboot \
            -i ${DEPLOY_DIR_IMAGE}/${UBOOT_BIN} \
            -o ${DEPLOY_DIR_IMAGE}/${UBOOT_EMMCBOOT} \
            -l ${UBOOT_EMMC_LOAD_ADDR} \
            -e ${UBOOT_EMMC_JUMP_ADDR}

        dd if=${DEPLOY_DIR_IMAGE}/${UBOOT_EMMCBOOT} of=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} seek=${UBBOT_FIP_NONSECURE_USB_BIN_OFFSET} bs=1

        # ===========================================
        # For usb download, create usb download image
        # ===========================================
        # step1. nsih-dummy.txt + fileSize + load/start address => nsih-usbdownload.txt
        # 1:${src_dir} |  2:${in_img} | 3:${dumy_file} | 4:${load_add} | 5:${start_add}
        do_nsihtxtmod ${DEPLOY_DIR_IMAGE} ${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} ${NEXELL_NSIHDUMMYFILE} ${NSIH_LOAD_START_ADDRESS} ${NSIH_LOAD_START_ADDRESS}

        # step2. nsih-usbdownload.bin
        # 1:${src_dir}
        do_nishbingen ${DEPLOY_DIR_IMAGE}

        # step3, create fip-loader-usb.img
        cp ${DEPLOY_DIR_IMAGE}/"nsih-usbdownload.bin" ${DEPLOY_DIR_IMAGE}/${FIP_LOADER_USB_IMG}
        dd if=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} >> ${DEPLOY_DIR_IMAGE}/${FIP_LOADER_USB_IMG}


        #local temp=""
        #temp+="abc"
    fi
}

addtask deploy after do_install
