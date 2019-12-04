require nexell-bl1-s5p4418-common.inc

COMPATIBLE_MACHINE = "s5p4418-daudio-ref"

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_compile () {
    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} BOARD="navi" KERNEL_VER="4" ARM_SECURE="n" CHIPNAME="nxp4330" DEVICE_PORT="0" SYSLOG="n" QUICKBOOT=${QUICKBOOT_ENABLE} -j 1
}

inherit deploy

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/bl1-navi.bin ${DEPLOY_DIR_IMAGE}/bl1-daudio.bin
    install -m 0644 ${S}/out/bl1-emmcboot.bin ${DEPLOY_DIR_IMAGE}
}

addtask deploy after do_install