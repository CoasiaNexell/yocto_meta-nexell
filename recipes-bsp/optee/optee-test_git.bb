DESCRIPTION = "OPTEE TEST for artik7"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README.md;md5=89ee7495ae9d82329fb68c61ed15a7a1"

SRCREV = "b28d0dadd467b7bcf13cb89deaec0d7ea122480b"
SRC_URI = "git://git.nexell.co.kr/nexell/secure/optee/optee_test;protocol=git;branch=artik"

S = "${WORKDIR}/git"

do_compile() {
    echo -e "\033[44;33m[suker][optee_test]${WORKDIR}/git/\033[0m" >> ${TOPDIR}/mylog.txt
}