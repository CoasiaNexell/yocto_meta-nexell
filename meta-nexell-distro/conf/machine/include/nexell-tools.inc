# For Make Image

# For Secure BIN
NEXELL_SECURE_TOOLS_PATH = "${BSP_ROOT_DIR}/layers/meta-nexell/meta-nexell-distro/tools/secure_tools"
NEXELL_CONVERT_TOOLS_PATH = "${BSP_ROOT_DIR}/layers/meta-nexell/meta-nexell-distro/tools/convert_tools"
NEXELL_NSIH_TOOLS_PATH = "${NEXELL_CONVERT_TOOLS_PATH}"

NEXELL_SECURE_BINGEN = "${NEXELL_SECURE_TOOLS_PATH}/SECURE_BINGEN"
NEXELL_RSA_SIGN_PSS = "${NEXELL_SECURE_TOOLS_PATH}/rsa_sign_pss"
NEXELL_NSIHTXTMOD = "${NEXELL_NSIH_TOOLS_PATH}/nsihtxtmod_yocto.py"
NEXELL_NSIHBINGEN = "${NEXELL_NSIH_TOOLS_PATH}/nsihbingen_yocto.py"
NEXELL_NSIHDUMMYFILE = "${NEXELL_NSIH_TOOLS_PATH}/nsih-dummy.txt"

#NEXELL_AES_KEY =
NEXELL_PRIVATE_KEY = "${NEXELL_SECURE_TOOLS_PATH}/private_key.pem"