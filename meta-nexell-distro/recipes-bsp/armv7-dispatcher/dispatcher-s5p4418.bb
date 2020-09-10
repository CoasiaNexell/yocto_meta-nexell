require dispatcher-s5p4418.inc

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_compile () {
    cd ${S}
    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} ${DISPATCHER_BUILD_CONFIG} QUICKBOOT=${QUICKBOOT_ENABLE} -j 1
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/${DISPATCHER_BIN} ${DEPLOY_DIR_IMAGE}

    # make secure image
    # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr} | 6:${extra_opts} | 7:${dev_id}
    make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/out/${DISPATCHER_BIN} \
            ${DEPLOY_DIR_IMAGE}/${DISPATCHER_EMMCBOOT} \
            ${DISPATCHER_EMMC_LOAD_ADDR} \
            ${DISPATCHER_EMMC_JUMP_ADDR} \
            '${DISPATCHER_EXTRA_OPTS}'

    # make fip image
    # 1:${in_img} |  2:${out_img} | 3:${seek_val} | 4:${bs_val}
    make_fip_image ${DEPLOY_DIR_IMAGE}/${DISPATCHER_EMMCBOOT} \
        ${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} \
        ${DISPATCHER_FIP_NONSECURE_USB_BIN_OFFSET} \
        "1"
}

addtask deploy after do_install
