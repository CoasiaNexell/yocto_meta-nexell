require dispatcher-s5p4418.inc

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_compile () {
    cd ${S}
    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} ${DISPATCHER_BUILD_CONFIG} QUICKBOOT=${QUICKBOOT_ENABLE} -j 1
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/${DISPATCHER_BIN} ${DEPLOY_DIR_IMAGE}

    # generate secure binary
    ${NEXELL_SECURE_BINGEN} -c ${NEXELL_BOARD_SOCNAME} \
            -t 3rdboot \
            -i ${DEPLOY_DIR_IMAGE}/${DISPATCHER_BIN} \
            -o ${DEPLOY_DIR_IMAGE}/${DISPATCHER_EMMCBOOT} \
            -l ${DISPATCHER_EMMC_LOAD_ADDR} \
            -e ${DISPATCHER_EMMC_JUMP_ADDR} \
            ${DISPATCHER_EXTRA_OPTS}

    dd if=${DEPLOY_DIR_IMAGE}/${DISPATCHER_EMMCBOOT} of=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} seek=${DISPATCHER_FIP_NONSECURE_USB_BIN_OFFSET} bs=1
}

addtask deploy after do_install
