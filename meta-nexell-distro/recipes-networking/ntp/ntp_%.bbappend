FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://ntpdate.default \
"

do_install_append() {
	install -d ${D}/${sysconfdir}/default
	install -m 0644 ${WORKDIR}/ntpdate.default ${D}${sysconfdir}/default/ntpdate
}
