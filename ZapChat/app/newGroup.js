import { FlashList } from "@shopify/flash-list";
import { Image } from "expo-image";
import { Alert, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { GroupCard } from "../components/GroupCard";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";
import { InputField } from "../components/InputField";
import { useEffect, useState } from "react";
import { router, useGlobalSearchParams, useLocalSearchParams } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import Modal from "react-native-modal";


const searchIcon = require("../assets/images/search.svg")
const newGroupIcon = require("../assets/images/team.png")

export default function newGroup() {

    const { data } = useGlobalSearchParams()

    const [getDataArray, setDataArray] = useState([])
    const [getSearchGroupName, setSearchGroupName] = useState("")
    const [getNewGroupName, setNewGroupName] = useState("")
    const [getUser, setUser] = useState("")
    const [getIsFound, setIsFound] = useState(false)
    const [getNewGroupModalStatus, setNewGroupModalStatus] = useState(false)
    const [getGroupImage, setGroupImage] = useState("../assets/images/team.png")


    useEffect(() => {
        (async () => {

            let verified = await AsyncStorage.getItem("verified");
            let user = await AsyncStorage.getItem("user");

            if (verified == null || verified != "true" || user == null) {

                await AsyncStorage.removeItem("verified")
                await AsyncStorage.removeItem("user")

                router.replace("/")
            } else {
                setUser(user)
            }

            let dataObj = JSON.parse(data)

            setDataArray(dataObj)
            if (dataObj.length > 0) {
                setIsFound(true)
            } else {

                let url = process.env.EXPO_PUBLIC_URL + "/Home"

                let obj = {
                    searchText: "",
                    category: "group"
                }
                let response = await fetch(url, {
                    method: "POST",
                    body: JSON.stringify(obj),
                    headers: {
                        "Content-Type": "application/json",
                        'Cookie': `JSESSIONID=${getUser}`
                    }
                })

                if (response.ok) {

                    let obj = await response.json()
                    if (obj.success) {

                        setDataArray(obj.data.data)
                        setIsFound(obj.data.isFound)

                    } else {
                        if (obj.data == "Please LogIn") {

                            await AsyncStorage.removeItem("verified");
                            await AsyncStorage.removeItem("user");

                            router.replace("/")
                        } else {
                            Alert.alert(obj.data);
                        }
                        console.log(obj.data)
                    }

                } else {
                    Alert.alert("Please Try Again Later");
                    console.log(response)
                }

            }
        })()
    }, [])

    const search = async () => {

        let url = process.env.EXPO_PUBLIC_URL + "/NewGroup"

        if (getSearchGroupName.length < 45) {

            let obj = {
                name: getSearchGroupName,
            }
            let response = await fetch(url, {
                method: "POST",
                body: JSON.stringify(obj),
                headers: {
                    "Content-Type": "application/json",
                    'Cookie': `JSESSIONID=${getUser}`
                }
            })

            if (response.ok) {

                let obj = await response.json()
                console.log(obj)
                if (obj.success) {

                    if (obj.data.isFound) {

                        setIsFound(true)
                        setDataArray(obj.data.data)

                    } else {
                        setDataArray([])
                    }

                } else {
                    if (obj.data == "Please LogIn") {

                        await AsyncStorage.removeItem("verified");
                        await AsyncStorage.removeItem("user");

                        router.replace("/")
                    } else {
                        Alert.alert(obj.data);
                    }
                    console.log(obj.data)
                }

            } else {
                Alert.alert("Please Try Again Later");
                console.log(response)
            }
        } else {
            Alert.alert("Group Name Too Long");
        }

    }

    const addNewGroup = async () => {

        try {

            if (getUser == "") {
                let sessionId = await AsyncStorage.getItem("user")
                if (sessionId == null) {

                    await AsyncStorage.removeItem("verified");
                    await AsyncStorage.removeItem("user");

                    router.replace("/")
                } else {
                    setUser(sessionId)
                }
            }

            if (getGroupImage.assets != null) {

                let imageTypeArr = [".png", ".jpg", ".jpeg"]

                if (getGroupImage.assets[0].uri.trim() == "") {
                    Alert.alert("Missing Image")

                } else if (getGroupImage.assets[0].type != "image") {
                    Alert.alert("Not a Image")

                } else if (!imageTypeArr.includes(getGroupImage.assets[0].uri.slice(getGroupImage.assets[0].uri.lastIndexOf('.')).toLowerCase())) {
                    Alert.alert("Invalid Image Type")

                } else if (getNewGroupName.trim() == "") {
                    Alert.alert("1: Missing Group Name")

                } else if (getNewGroupName.length > 45) {
                    Alert.alert("Group Name Too Long")

                } else if (getGroupImage.assets == null) {
                    Alert.alert("Missing Assests")
                } else {

                    let extention;
                    let invalid = false;

                    if (getGroupImage.assets[0].mimeType == "image/jpeg") {
                        extention = ".jpeg"

                    } else if (getGroupImage.assets[0].mimeType == "image/jpg") {
                        extention = ".jpg"

                    } else if (getGroupImage.assets[0].mimeType == "image/png") {
                        extention = ".png"

                    } else {
                        Alert.alert("Invalid Extention")
                        invalid = true
                    }

                    if (!invalid) {

                        let sessionId = getUser
                        let url = process.env.EXPO_PUBLIC_URL + "/AddNewGroup"

                        let formData = new FormData()
                        formData.append("groupName", getNewGroupName)
                        formData.append("image", {
                            name: "profileImage" + extention,
                            type: getGroupImage.assets[0].mimeType,
                            uri: getGroupImage.assets[0].uri,
                        })
                        formData.append("extention", extention)
                        console.log("h: " + getNewGroupName)


                        let response = await fetch(url, {
                            method: "POST",
                            body: formData,
                            headers: {
                                'Cookie': `JSESSIONID=${sessionId}`
                            }
                        })
                        if (response.ok) {

                            let obj = await response.json()
                            if (obj.success) {


                                // console.log("obj: "+JSON.stringify(obj))
                                // console.log("obj data: "+JSON.stringify(obj.data))
                                // console.log("obj data data: "+JSON.stringify(obj.data.data))
                                // await AsyncStorage.setItem("profileImage", JSON.stringify(obj.data))
                                // await AsyncStorage.setItem("profileAbout", JSON.stringify(getAbout))
                                router.push({ pathname: "/singleGroup", params: obj.data })
                                // Alert.alert("Profile Update Success");

                            } else {
                                Alert.alert(obj.data);
                                console.log(obj.data)
                            }

                        } else {
                            Alert.alert("Please Try Again Later");
                            console.log(response)
                        }
                    }
                }
            }

        } catch (error) {
            console.log(error)
        }
    }

    return (
        <SafeAreaView style={styles.body}>

            <View style={styles.head}>
                <TextInput
                    placeholder="Enter Group Name .."
                    numberOfLines={1}
                    style={styles.input}
                    onChangeText={(text) => { setSearchGroupName(text) }}
                    value={getSearchGroupName}
                    inputMode="text"
                />

                <Pressable onPress={search}>
                    <Image source={searchIcon} style={styles.icon} />
                </Pressable>
            </View>

            <Modal
                isVisible={getNewGroupModalStatus}
                // isVisible={true}
                onBackButtonPress={() => { setNewGroupModalStatus(false) }}
                onBackdropPress={() => { setNewGroupModalStatus(false) }}
                style={styles.modal}
            >

                <View style={styles.modalView}>
                    <Text style={styles.title}>Create New group</Text>
                    <Profile getFunc={getGroupImage} setFunc={setGroupImage} icon={newGroupIcon} text={"Group icon"} style={styles.profileview} />
                    <View style={styles.bottom}>
                        <InputField params={{ lableText: "Group Name", secureTextEntry: false, inputMode: "text", maxLength: 45, func: setNewGroupName, getFunc: getNewGroupName }} />
                        <Button text={"Create +"} style={[styles.newButton, styles.create]} func={addNewGroup} />
                    </View>
                </View>


            </Modal>

            {getNewGroupModalStatus ? "" : (
                <Button text={"Create a New Group"} style={styles.newButton} func={() => { setNewGroupModalStatus(true) }} />
            )}

            {getIsFound ? (
                <View style={styles.listView}>
                    <FlashList
                        renderItem={({ item }) => <GroupCard data={item} />}
                        data={getDataArray}
                        keyExtractor={item => item.groupId}
                        // contentContainerStyle={styles.list}
                        estimatedItemSize={200}
                    />
                </View>
            ) : (
                <View style={styles.noView}>
                    <Text style={styles.noText}>No Groups !</Text>
                </View>
            )}

        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    listView: {
        flexGrow: 1,
        // backgroundColor: "rgb(245, 245, 245)"
    },
    profileview: {
        borderColor: "#d1d1d1",
        borderWidth: 3,
        borderStyle: "solid"
    },
    title: {
        color: "#ff5b6b",
        fontWeight: "bold",
        fontSize: 20,
        letterSpacing: 1.5,
        alignSelf: "flex-start",
        justifyContent: "flex-start",
        // backgroundColor:"green",
        marginTop: -100,
        marginBottom: 50
    },
    bottom: {
        marginTop: 60,
        width: "100%"
    },
    modal: {
        backgroundColor: "white",
        margin: 0,
        justifyContent: "center",
        alignItems: "center"
    },
    create: {
        marginTop: 30,
        width: "100%"
    },
    modalView: {
        justifyContent: "center",
        alignItems: "center",
        width: "80%",
        // backgroundColor:"red",
        flex: 1
    },
    newButton: {
        alignSelf: "center",
        borderRadius: 5,
        width: "92%",
        marginBottom: 25
    },
    noText: {
        fontSize: 20,
        fontWeight: "bold",
        color: "#919190"
    },
    noView: {
        width: "100%",
        alignItems: "center",
        flex: 1,
        justifyContent: "center",
        backgroundColor: "rgb(235, 235, 235)",
    },
    icon: {
        width: 22,
        height: 22,
    },
    head: {
        flexDirection: "row",
        columnGap: 15,
        justifyContent: "center",
        alignItems: "center",
        marginBottom: 20
    },
    input: {
        backgroundColor: "#e3e3e3",
        borderRadius: 5,
        minHeight: 40,
        maxHeight: 120,
        paddingHorizontal: 10,
        color: "#ff5b6b",
        width: "80%",
        paddingVertical: 2
    },
    body: {
        flex: 1
    }
})