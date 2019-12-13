DESCRIPTION = "Nexell user data image"
LICENSE = "GPLv2+"

inherit nexell-mkimage

do_deploy() {
	make_data_image
}
addtask deploy after do_package

# not execute tasks
do_packagedata[noexec] = "1"
do_package_qa[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_prepare_recipe_sysroot[noexec] = "1"
do_populate_sysroot[noexec] = "1"

# always run task
do_deploy[nostamp] = "1"
