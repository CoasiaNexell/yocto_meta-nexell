inherit post-process-qt
inherit post-process
require recipes-graphics/images/core-image-weston.bb

DESCRIPTION ?= "Nexell vapor gang writer's image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

IMAGE_FEATURES += "package-management ssh-server-dropbear splash"

SYSTEMD_INSTALL = " \
	systemd-compat-units \
	rsyslog-systemd \
"

DEVEL_TOOLS = " \
	android-tools-nexell \
"

TOUCH_IMAGE_INSTALL = "${@bb.utils.contains('DISTRO_FEATURES', 'no-use-tslib', '', 'tslib tslib-conf tslib-tests tslib-calibrate tslib-nexell', d)}"

ETC_TOOLS = " \
	tinyalsa \
	udev-extraconf \
	tzdata \
	net-tools \
	openssl \
	curl \
	procps \
	dlt-daemon \
"

ETC_LIBS = " \
	icu \
"

NEXELL_CUSTOMIZE_INSTALL = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'qt-default-platform-eglfs', 'eglfs-kms', '', d)} \
	user-fonts \
	nexell-qt5-touchsetup \
"

IMAGE_INSTALL_append = " \
	kernel-modules \
	${SYSTEMD_INSTALL} \
	${DEVEL_TOOLS} \
	packagegroup-nexell-qt-lite \
	weston-init \
	rtl-8188eus-${ARCH_TYPE_NUM} \
	${TOUCH_IMAGE_INSTALL} \
	${ETC_TOOLS} \
	${ETC_LIBS} \
	${NEXELL_CUSTOMIZE_INSTALL} \
"
