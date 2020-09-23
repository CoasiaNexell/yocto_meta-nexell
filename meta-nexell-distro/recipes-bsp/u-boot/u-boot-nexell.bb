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

inherit nexell-mkimage

do_deploy () {
    export PATH=$PATH:/usr/bin
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/${UBOOT_BIN} ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/default_envs.txt ${DEPLOY_DIR_IMAGE}

    if [ ${NEXELL_BOARD_SOCNAME} != "s5p6818" ]; then
        # make emmc boot image
        # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr}
        make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/${UBOOT_BIN} \
            ${DEPLOY_DIR_IMAGE}/${UBOOT_EMMCBOOT} \
            ${UBOOT_EMMC_LOAD_ADDR} \
            ${UBOOT_EMMC_JUMP_ADDR}

        # make fip loader image for usb
        make_fip_loader_usb_image
    fi

    # make env image
    # 1:${partition_size} |  2:${out_img} | 3:${envs_file}
    make_environment_image ${NEXELL_ENV_PARTITION_SIZE} \
            ${DEPLOY_DIR_IMAGE}/params.bin \
            ${DEPLOY_DIR_IMAGE}/default_envs.txt
}

addtask deploy after do_install
