LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://pyrope_bl2.lds;md5=c81ae199c8ac9bc25a4389a9c6b387b6"
PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_URI = "git://github.com/CoasiaNexell/bl2_s5p4418;protocol=https;branch=nexell"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS = "arm-eabi-4.8"
TOOLCHAIN_ARCH32_EABI = "${RECIPE_SYSROOT}${datadir}/arm-eabi-4.8-toolchain/bin/"

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_compile () {
    cd ${S}
    oe_runmake CROSS_COMPILE_TOP=${TOOLCHAIN_ARCH32_EABI} QUICKBOOT=${QUICKBOOT_ENABLE} -j 1
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/pyrope-bl2.bin ${DEPLOY_DIR_IMAGE}

    # generate secure binary
    ${NEXELL_SECURE_BINGEN} -c ${NEXELL_BOARD_SOCNAME} \
            -t 3rdboot \
            -i ${DEPLOY_DIR_IMAGE}/${BL2_BIN} \
            -o ${DEPLOY_DIR_IMAGE}/${BL2_EMMCBOOT} \
            -l ${BL2_EMMC_LOAD_ADDR} \
            -e ${BL2_EMMC_JUMP_ADDR} \
            ${BL2_EXTRA_OPTS}

    dd if=${DEPLOY_DIR_IMAGE}/${BL2_EMMCBOOT} of=${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} seek=0 bs=1
}

addtask deploy after do_install
