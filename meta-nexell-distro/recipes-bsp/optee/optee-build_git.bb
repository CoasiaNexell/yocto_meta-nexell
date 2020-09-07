DESCRIPTION = "OPTEE BUILD"
inherit nexell-source-path-extension
include optee-secure.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README.md;md5=871e3be979e189da85a03fe6ba47bbe2"

PV ?= "1.0+git${SRCPV}"
SRCREV = "04e84323aaaa30b992002c26c4e16a15809b5c7e"

SRC_URI = "git://github.com/CoasiaNexell/secure_optee_optee-build;protocol=https;branch=nexell \
           file://0001-optee-build-customize-for-yocto.patch \
          "

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit pkgconfig
DEPENDS = "arm-trusted-firmware l-loader optee-os optee-client optee-test u-boot-nexell bl1-${OPTEE_BUILD_TARGET_SOCNAME}"

TOOLCHAIN_32 = "${BSP_VENDOR_DIR}/toolchain/gcc-linaro-4.9-2014.11-x86_64_arm-linux-gnueabihf/bin/arm-linux-gnueabihf-"
TOOLCHAIN_32_BIN_PATH = "${BSP_VENDOR_DIR}/toolchain/gcc-linaro-4.9-2014.11-x86_64_arm-linux-gnueabihf/bin"
TOOLCHAIN_32_LIB_PATH = "${BSP_VENDOR_DIR}/toolchain/gcc-linaro-4.9-2014.11-x86_64_arm-linux-gnueabihf/lib"
TOOLCHAIN_32_LIB_FLAGS = "-L${TOOLCHAIN_32_LIB_PATH}"

TOOLCHAIN_64 = "${BSP_VENDOR_DIR}/toolchain/gcc-linaro-4.9-2015.05-x86_64_aarch64-linux-gnu/bin/aarch64-linux-gnu-"
TOOLCHAIN_64_BIN_PATH = "${BSP_VENDOR_DIR}/toolchain/gcc-linaro-4.9-2015.05-x86_64_aarch64-linux-gnu/bin"
TOOLCHAIN_64_LIB_PATH = "${BSP_VENDOR_DIR}/toolchain/gcc-linaro-4.9-2015.05-x86_64_aarch64-linux-gnu/lib"
TOOLCHAIN_64_LIB_FLAGS = "-L${TOOLCHAIN_64_LIB_PATH}"

PATH_OPTEE_BUILD ?= "${@env_setup(d,"optee-build")}"
PATH_OPTEE_OS ?= "${@env_setup(d,"optee-os")}"
PATH_OPTEE_CLIENT ?= "${@env_setup(d,"optee-client")}"
PATH_OPTEE_LINUXDRIVER ?= "${@env_setup(d,"optee-linuxdriver")}"
PATH_OPTEE_TEST ?= "${@env_setup(d,"optee-test")}"
PATH_ATF ?= "${@env_setup(d,"arm-trusted-firmware")}"
PATH_L-LOADER ?= "${@env_setup(d,"l-loader")}"
PATH_U-BOOT ?= "${@env_setup(d,"u-boot-nexell")}"
PATH_BL1 ?= "${@env_setup(d,"bl1-${OPTEE_BUILD_TARGET_SOCNAME}")}"
PATH_KERNEL_SRC ?= "${@env_setup(d,"kernel-source")}"
PATH_KERNEL_BUILD = "${@env_setup(d,"kernel-build-artifacts")}"


COMMON_FLAGS = ' \
    PLAT_DRAM_SIZE=${OPTEE_PLAT_DRAM_SIZE} \
    PLAT_UART_BASE=${OPTEE_PLAT_UART_BASE} \
    ${SECURE-OPTEE} \
    CROSS_COMPILE=${TOOLCHAIN_64} \
    CROSS_COMPILE32=${TOOLCHAIN_32} \
    YOCTO_DTB=${KERNEL_DTB_NAME} \
    YOCTO_DEFCONFIG=${KBUILD_DEFCONFIG} \
    UBOOT_DIR=${S}/u-boot-artik7 \
    CPPFLAGS+="-fno-strict-aliasing" \
    DISABLE_PEDANTIC=1 \
'

python do_make_source_dir_pathfile() {
    import subprocess

    base_workdir = d.getVar("BASE_WORKDIR", d, True)
    multimach_host_sys = d.getVar("MULTIMACH_TARGET_SYS", d, True)
    kernel_src_path = d.getVar("STAGING_KERNEL_DIR", d, True)
    kernel_build_path = d.getVar("STAGING_KERNEL_BUILDDIR", d, True)

    pv = d.getVar("PV", d, True)
    pr = d.getVar("PR", d, True)

    uboot_name = "u-boot-nexell"    #d.getVar("OPTEE_BUILD_TARGET_MACHINE", d, True)+"-uboot"
    bl1_name = "bl1-s5p6818"        #d.getVar("OPTEE_BUILD_TARGET_MACHINE", d, True)+"-bl1"
    kernel_name = "linux-s5p6818"   #d.getVar("OPTEE_BUILD_TARGET_SOCNAME", d, True)

    recipes_optee_paths = ["optee-build", "optee-os", "optee-client", "optee-linuxdriver", "optee-test"]
    recipes_lloader_paths = ["l-loader"]
    recipes_atf_paths = ["arm-trusted-firmware"]
    recipes_others = [bl1_name, uboot_name, kernel_name]

    recipes_all = recipes_optee_paths + recipes_lloader_paths + recipes_atf_paths + recipes_others

    subprocess.getoutput('echo %s > %s/SOURCE_PATH_FOR_OPTEE.txt' % ("For Yocto ${MACHINE_ARCH} optee build",base_workdir))
    for dirname in recipes_all :
        if dirname == kernel_name :
            subprocess.getoutput('echo %s >> %s/SOURCE_PATH_FOR_OPTEE.txt' % (kernel_src_path, base_workdir))
            subprocess.getoutput('echo %s >> %s/SOURCE_PATH_FOR_OPTEE.txt' % (kernel_build_path, base_workdir))
        else :
            temp = str(base_workdir + "/" + multimach_host_sys + "/" + dirname + "/" + pv + "-" + pr + "/git")
            subprocess.getoutput('echo %s >> %s/SOURCE_PATH_FOR_OPTEE.txt' % (temp, base_workdir))
}

do_make_symlink() {
    ln -sf "${PATH_OPTEE_OS}" ${S}/optee_os
    ln -sf "${PATH_OPTEE_CLIENT}" ${S}/optee_client
    ln -sf "${PATH_OPTEE_LINUXDRIVER}" ${S}/optee_linuxdriver
    ln -sf "${PATH_OPTEE_TEST}" ${S}/optee_test
    ln -sf "${PATH_ATF}" ${S}/arm-trusted-firmware
    ln -sf "${PATH_L-LOADER}" ${S}/l-loader
    ln -sf "${PATH_U-BOOT}" ${S}/u-boot-artik7
    ln -sf "${PATH_BL1}" ${S}/bl1-artik7

    if [ ${LOCAL_KERNEL_SOURCE_USING} = "true" ];then
        ln -sf "${PATH_KERNEL_SRC}" ${S}/linux-artik7
    else
        ln -sf "${PATH_KERNEL_BUILD}" ${S}/linux-artik7
    fi
}

TARGET_CPPFLAGS = ""
TARGET_CFLAGS = ""
TARGET_CXXFLAGS = ""
TARGET_LDFLAGS = ""
SECURITY_LDFLAGS_pn-optee-build = " "

do_compile() {
	export PATH=${TOOLCHAIN_32_BIN_PATH}:/usr/bin/:$PATH
    export LDFLAGS=""

    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} QUICKBOOT=${QUICKBOOT_ENABLE} clean
    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-bl1 QUICKBOOT=${QUICKBOOT_ENABLE} -j8
    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-lloader QUICKBOOT=${QUICKBOOT_ENABLE} -j8

    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-bl32 QUICKBOOT=${QUICKBOOT_ENABLE} -j8

    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-fip QUICKBOOT=${QUICKBOOT_ENABLE} -j8
    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-fip-loader QUICKBOOT=${QUICKBOOT_ENABLE} -j8
    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-fip-secure QUICKBOOT=${QUICKBOOT_ENABLE} -j8
    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-fip-nonsecure QUICKBOOT=${QUICKBOOT_ENABLE} -j8

    oe_runmake -f ${S}/Makefile ${COMMON_FLAGS} build-singleimage QUICKBOOT=${QUICKBOOT_ENABLE} -j8
}

do_install() {
	rm -rf ${D}/lib/optee_armtz
	mkdir -p ${D}/lib/optee_armtz
	install -m 0755 ${S}/optee_build/result/fip-loader.bin ${D}/lib/optee_armtz
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/optee_build/result/fip-loader.bin ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/optee_build/result/fip-nonsecure.bin ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/optee_build/result/fip-secure.bin ${DEPLOY_DIR_IMAGE}

    if [ ${NEXELL_BOARD_SOCNAME} = "s5p6818" ];then
        # make image for emmc
        # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr} | 6:${extra_opts} | 7:${dev_id}
        make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/optee_build/result/fip-loader.bin \
            ${DEPLOY_DIR_IMAGE}/fip-loader-emmc.img \
            ${MEM_LOAD_ADDR} \
            ${MEM_JUMP_ADDR} \
            '${NEXELL_EMMC_EXTRA_OPTS}' \
            ${NEXELL_DEVID_SDMMC}

        # make image for sd
        # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr} | 6:${extra_opts} | 7:${dev_id}
        make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/optee_build/result/fip-loader.bin \
            ${DEPLOY_DIR_IMAGE}/fip-loader-sd.img \
            ${MEM_LOAD_ADDR} \
            ${MEM_JUMP_ADDR} \
            '${NEXELL_EMMC_EXTRA_OPTS}' \
            ${NEXELL_DEVID_SD}

        # make image for secure
        # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr}
        make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/optee_build/result/fip-secure.bin \
            ${DEPLOY_DIR_IMAGE}/fip-secure.img \
            ${MEM_SECURE_LOAD_ADDR} \
            ${MEM_SECURE_JUMP_ADDR}

        # get size of image
        local fip_sec_size=`stat --printf="%s" ${DEPLOY_DIR_IMAGE}/fip-secure.img`

        # make image for non-secure
        # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr}
        make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/optee_build/result/fip-nonsecure.bin \
            ${DEPLOY_DIR_IMAGE}/fip-nonsecure.img \
            ${MEM_NON_SECURE_LOAD_ADDR} \
            ${MEM_NON_SECURE_JUMP_ADDR}

        # get size of image
        local fip_nonsec_size=`stat --printf="%s" ${DEPLOY_DIR_IMAGE}/fip-nonsecure.img`

        # gen extra opts for usb
        local usb_extra_opts="-u -m ${MEM_SECURE_LOAD_ADDR} -z ${fip_sec_size} -m ${MEM_NON_SECURE_LOAD_ADDR} -z ${fip_nonsec_size}"

        # make image for usb
        # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr} | 6:${extra_opts} | 7:${dev_id}
        make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/optee_build/result/fip-nonsecure.bin \
            ${DEPLOY_DIR_IMAGE}/fip-nonsecure.img \
            ${MEM_NON_SECURE_LOAD_ADDR} \
            ${MEM_NON_SECURE_JUMP_ADDR} \
            "${usb_extra_opts}" \
            ${NEXELL_DEVID_USB}
    fi
}

FILES_${PN} += " \
        ${bindir}/* \
        ${libdir}/* \
        /lib/optee_armtz \
        "
INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-dev += "dev-elf ldflags"

addtask make_source_dir_pathfile before do_configure
addtask make_symlink after do_unpack before do_configure
#addtask make_symlink after do_configure before do_compile
addtask deploy after do_install
