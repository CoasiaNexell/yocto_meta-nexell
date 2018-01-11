SUMMARY = "Qt5 APP LIBS, NEXELL"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "qtbase qtmultimedia nx-video-api nx-v4l2 libdrm nx-vidtex ffmpeg"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'qtwayland', '', d)}"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.4', 'qtquickcontrols', 'qtquickcontrols2', d)}"

SRCREV = "bee9d7bf3443068ccaf15ca801393965c1e640fe"
SRC_URI = "git://git.nexell.co.kr/nexell/linux/apps/QT/NxVideoPlayer;protocol=git;branch=master"

S = "${WORKDIR}/git"

PV = "NEXELL"
PR = "0.1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools pkgconfig
require recipes-qt/qt5/qt5.inc
#inherit linux-nexell-base

#HOST_SYS ==> arm-poky-linux-gnueabi
#oe_runconf is run at WORKDIR, so can't use oe_runconf. If want use oe_runconf, configure file has to located WORKDIR

#-----work flow-----
#STAGING_INCDIR=/home/suker/repoYocto/yocto/build-s5p4418-navi-ref-qt/tmp/sysroots/s5p4418-navi-ref/usr/include

do_videoPlayer_precompile() {
    ln -sf ${S}/libnxplayer ${B}/
}

do_install() {
    install -d ${D}/podo/apps/NxVideoPlayer
    install -d ${D}${libdir}
    install -m 0644 ${S}/Package/* ${D}/podo/apps/NxVideoPlayer
    install ${B}/NxVideoPlayer/NxVideoPlayer ${D}/podo/apps/NxVideoPlayer
    if [ "${ARCH_TYPE_NUM}" -eq "32" ]; then
        install -m 0644 ${B}/libnxplayer/lib/32bit/*.so ${D}${libdir}
    else
        echo "64bit libnxsubtitle todo"
    fi
}

addtask videoPlayer_precompile before do_compile after do_configure

FILES_${PN} = "podo ${libdir}"
RDEPENDS_${PN} = "qtbase-plugins"
INSANE_SKIP_${PN}-dev = "dev-elf"
ERROR_QA_remove = "compile-host-path"
RDEPENDS_${PN} += "libavformat libavcodec libavdevice libavfilter"