require nexell-dispatcher-s5p4418-common.inc

COMPATIBLE_MACHINE = "s5p4418-smart-voice"

TOOLCHAIN_ARCH32_EABI = "${BASE_WORKDIR}/arm-eabi-4.8/bin/"

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_compile () {
    cd ${S}
    oe_runmake CROSS_TOOL_TOP=${TOOLCHAIN_ARCH32_EABI} BOARD="SMARTVOICE" -j 1
}

inherit deploy

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/armv7_dispatcher-smartvoice.bin ${DEPLOY_DIR_IMAGE}
}

addtask deploy after do_install