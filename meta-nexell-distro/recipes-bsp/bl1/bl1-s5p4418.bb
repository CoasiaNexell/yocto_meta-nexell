LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://README;md5=c48cff868868eb40f73e510f7bae7710"
PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_URI = "git://github.com/CoasiaNexell/bl1_s5p4418;protocol=https;branch=nexell"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

BL1_TOOLCHAIN = "${BSP_VENDOR_DIR}/toolchain/arm-eabi-4.8/bin/arm-eabi-"


EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_compile () {
	oe_runmake CROSS_COMPILE=${BL1_TOOLCHAIN} ${BL1_BUILD_CONFIG} QUICKBOOT=${QUICKBOOT_ENABLE} -j 1
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/${BL1_BIN} ${DEPLOY_DIR_IMAGE}/${BL1_FINAL_BIN}
    install -m 0644 ${S}/out/${BL1_EMMCBOOT} ${DEPLOY_DIR_IMAGE}

    # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${aes_key} | 5:${hash_name}
    make_2ndboot_image ${NEXELL_BOARD_SOCNAME} \
            ${DEPLOY_DIR_IMAGE}/${BL1_EMMCBOOT} \
            ${DEPLOY_DIR_IMAGE}/${BL1_SECURE_EMMCBOOT} \
            ${NEXELL_PRIVATE_KEY}

    # copy image to output dir
    copy_file_to_output ${S}/out/${BL1_EMMCBOOT}
}

addtask deploy after do_install
