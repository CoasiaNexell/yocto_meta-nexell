DESCRIPTION = "Nexell daudio SDK"
HOMEPAGE = "http://www.nexell.co.kr"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/solutions/displayaudio-sdk"

SRC_URI = "file://${SRC_PATH}"

DEPENDS = " \
	libxml2 \
	nx-drm-allocator \
	nx-video-api \
	nx-scaler \
	nx-v4l2 \
	nx-uds \
	nx-config \
	ffmpeg \
	id3lib \
	libid3tag \
	libjpeg-turbo \
	taglib \
	icu \
	qtbase-native \
	qtmultimedia \
	gstreamer1.0-plugins-base \
"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.14', 'nexell-sdk-qt5.14.x-env', '', d)}

PV = "1.4.0"
PR = "r0"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"
SDK_RESULT = "${S}/result"

export OECORE_SDK_VERSION = "${SDK_VERSION}"
export OECORE_SOCNAME = "${NEXELL_BOARD_SOCNAME}"
export TARGET_MACHINE = "${MACHINE}"
export NX_DAUDIO_ENABLE_BT = "${ENABLE_BT}"
export NX_DAUDIO_ENABLE_CAM = "${ENABLE_CAM}"

D_SDK_INC += " -I${STAGING_INCDIR}/libxml2 -I${STAGING_INCDIR}/drm"

CFLAGS_prepend = "${D_SDK_INC}"
CXXFLAGS_prepend = "${D_SDK_INC}"
CFLAGS_append = "  -Wno-format-security "
CXXFLAGS_append = "  -Wno-format-security "

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_install() {
	echo "Installing daudio SDK..."

	install -d ${D}${libdir}
	install -d ${D}${bindir}
	install -d ${D}/nexell/daudio

	cp -apR ${SDK_RESULT}${libdir}/* ${D}${libdir}/
	chown -R root:root ${D}${libdir}/
	cp -apR ${SDK_RESULT}${bindir}/* ${D}${bindir}/
	chown -R root:root ${D}${bindir}/
	cp -apR ${SDK_RESULT}/nexell/daudio/* ${D}/nexell/daudio/
	chmod -R 755 ${D}/nexell/daudio/*
	chown -R root:root ${D}/nexell/daudio/*
}

FILES_${PN} = "${bindir} ${libdir} nexell/daudio"
FILES_${PN}-dev = "${includedir}"

INSANE_SKIP_${PN} = "dev-deps dev-so textrel already-stripped"
INSANE_SKIP_${PN}-dev = "dev-elf textrel"
