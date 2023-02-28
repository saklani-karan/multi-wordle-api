import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { MainLayout } from "../../layouts/MainLayout";
import { client } from "../../utils";
import { useUserAuthContext } from "../../context/UserAuthProvider";
import { BlockOptions } from "../../components/form";
import { over } from "stompjs";
import SockJS from "sockjs-client";
import { Check } from "../../icons";

let stompClient = null;
export default function Room() {
    const router = useRouter();
    const authContext = useUserAuthContext();
    const [roomData, setRoomData] = useState({
        room: null,
        isAdmin: false,
        readyToStart: false,
        isReady: false,
    });
    const [room, setRoom] = useState(null);
    const [user, setUser] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [gameSettings, setGameSettings] = useState({
        numberRounds: 1,
        roundMode: "TIME_BASED",
    });

    const getRoom = async () => {
        const query = router.query;
        if (query?.id && authContext.getToken()) {
            try {
                const { prevRoom, roomUserData, roomCreationValidation } =
                    await getRoomData({});
                const prevRoomData = {
                    room: prevRoom,
                    isAdmin: roomUserData?.isAdmin,
                    isReady: roomUserData?.isReady,
                    readyToStart: roomCreationValidation?.success,
                };
                setUser(authContext.getUser());
                setRoomData(prevRoomData);
            } catch (err) {}
        }
    };

    const getRoomData = async ({
        fetchRoom = true,
        fetchRoomUser = true,
        fetchRoomCreationValidation = true,
    }) => {
        const query = router.query;
        const [prevRoom, roomUserData, roomCreationValidation] =
            await Promise.all([
                fetchRoom
                    ? await client(
                          `${process.env.NEXT_PUBLIC_API_URL}/rooms/${query.id}`,
                          {
                              mode: "cors",
                              headers: {
                                  "Content-Type": "application/json",
                                  Authorization: `Bearer ${authContext.getToken()}`,
                              },
                          }
                      )
                    : null,
                fetchRoomUser
                    ? await client(
                          `${process.env.NEXT_PUBLIC_API_URL}/rooms/${query.id}/me`,
                          {
                              mode: "cors",
                              headers: {
                                  "Content-Type": "application/json",
                                  Authorization: `Bearer ${authContext.getToken()}`,
                              },
                          }
                      )
                    : null,
                fetchRoomCreationValidation
                    ? await client(
                          `${process.env.NEXT_PUBLIC_API_URL}/rooms/${query.id}/validateGameCreation`,
                          {
                              mode: "cors",
                              headers: {
                                  "Content-Type": "application/json",
                                  Authorization: `Bearer ${authContext.getToken()}`,
                              },
                          }
                      )
                    : null,
            ]);
        return {
            prevRoom,
            roomUserData,
            roomCreationValidation,
        };
    };

    const connect = () => {
        let Sock = new SockJS(process.env.NEXT_PUBLIC_WS_URL);
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    };

    const userJoin = () => {
        const chatMessage = {
            userId: user?.id,
            roomId: roomData?.room?.id,
            status: "JOIN",
        };
        stompClient.send("/app/room/join", {}, JSON.stringify(chatMessage));
    };

    const toggleReadyStatus = () => {
        const chatMessage = {
            userId: user?.id,
            roomId: roomData?.room?.id,
            status: roomData.isReady ? "NOT_READY" : "READY",
        };
        stompClient.send(
            "/app/room/statusUpdate",
            {},
            JSON.stringify(chatMessage)
        );
    };

    const onConnected = () => {
        setTimeout(function () {
            stompClient.subscribe(
                "/room/" + roomData?.room?.id + "/user_join",
                onRoomJoinMessage
            );
            stompClient.subscribe(
                "/room/" + roomData?.room?.id + "/user_status_update",
                onUserStatusUpdate
            );
            stompClient.subscribe(
                "/room/" + roomData?.room?.id + "/game_created",
                onGameCreation
            );
            setIsConnected(true);
            userJoin();
        }, 500);
    };

    const onGameCreation = (payload) => {
        const message = JSON.parse(payload.body);
        const { gameId, roomId } = message;
        router.push(`/game/${gameId}`);
    };

    const onRoomJoinMessage = (payload) => {
        const message = JSON.parse(payload.body);
        switch (message?.status) {
            case "JOIN":
                handleUserJoin(message);
                break;
            default:
                console.log("unhandled message received");
        }
    };

    const onUserStatusUpdate = async (payload) => {
        const message = JSON.parse(payload.body);
        const { prevRoom, roomUserData, roomCreationValidation } =
            await getRoomData({});
        const prevRoomData = {
            ...roomData,
            room: prevRoom,
            isReady: roomUserData?.isReady,
            readyToStart: roomCreationValidation?.success,
        };
        setRoomData(prevRoomData);
    };

    const handleUserJoin = async ({ userId }) => {
        if (userId === user?.id) {
            return;
        }
        const { prevRoom, roomCreationValidation } = await getRoomData({
            fetchRoomUser: false,
        });
        const prevRoomData = {
            ...roomData,
            room: prevRoom,
            readyToStart: roomCreationValidation?.success,
        };
        setRoomData(prevRoomData);
    };

    const onError = (err) => {
        console.log(err);
    };
    const handleFormEntry = (label, value) => {
        const prevSettings = { ...gameSettings };
        setGameSettings({
            ...prevSettings,
            [label]: value,
        });
    };

    const createGame = async () => {
        try {
            const game = await client(
                `${process.env.NEXT_PUBLIC_API_URL}/games/create`,
                {
                    mode: "cors",
                    method: "post",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${authContext.getToken()}`,
                    },
                    body: JSON.stringify({
                        ...gameSettings,
                        roomId: roomData?.room?.id,
                    }),
                }
            );
            stompClient.send(
                "/app/room/gameCreated",
                {},
                JSON.stringify({ roomId: roomData?.room?.id, gameId: game?.id })
            );
            router.push(`/game/${game?.id}`);
        } catch (err) {}
    };

    useEffect(() => {
        getRoom();
    }, [router.query, authContext.getToken()]);

    useEffect(() => {
        if (user && roomData && !isConnected) {
            connect();
        }
    }, [user, roomData]);

    return (
        <MainLayout>
            {!roomData?.room ? (
                <div></div>
            ) : (
                <div className="flex-col flex space-y-6">
                    <div className="flex-col flex space-y-2">
                        <div className="flex justify-center items-center text-lg font-bold">
                            Room Number: {roomData?.room?.id}
                        </div>
                        <div className="flex justify-center items-center text-lg font-bold">
                            Room Users
                        </div>
                        <div className="flex justify-center space-x-4">
                            {roomData?.room?.users?.map((user) => {
                                const roomUser = user?.user;
                                const userStatus = user?.status;
                                const userRole = user?.role;
                                return (
                                    <div className="flex flex-col w-1/3 justify-center items-center rounded-lg bg-secondary shadow-lg py-4 px-2 space-y-2 relative">
                                        {userStatus === "READY" ? (
                                            <div className="absolute top-2 right-2 flex w-5 h-5 text-correct">
                                                <Check />
                                            </div>
                                        ) : null}
                                        <img
                                            className="w-20 h-20 rounded-full"
                                            src={roomUser.picture}
                                        />
                                        <p className="text-base font-semibold">
                                            {roomUser.name}
                                        </p>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                    {roomData?.isAdmin ? (
                        <div className="flex-col flex space-y-4">
                            <div className="flex justify-center items-center text-lg font-bold">
                                Game Settings
                            </div>
                            <div className="flex flex-col justify-center items-center space-y-2">
                                <p className="font-light">Game Rounds</p>
                                <BlockOptions
                                    options={[
                                        { key: 1, value: 1 },
                                        { key: 3, value: 3 },
                                        { key: 5, value: 5 },
                                    ]}
                                    label="numberRounds"
                                    handleFormEntry={handleFormEntry}
                                />
                            </div>
                            <div className="flex flex-col justify-center items-center space-y-2">
                                <p className="font-light">Game Mode</p>
                                <BlockOptions
                                    options={[
                                        {
                                            key: "TIME_BASED",
                                            value: "Time based",
                                        },
                                        {
                                            key: "TRIAL_BASED",
                                            value: "Trials based",
                                        },
                                    ]}
                                    label="roundMode"
                                    handleFormEntry={handleFormEntry}
                                />
                            </div>
                        </div>
                    ) : null}

                    <div className="flex flex-col space-y-2 justify-center items-center">
                        <button
                            className="flex justify-center items-center space-x-2 p-4 px-6 md:px-8 font-medium bg-correct cursor-pointer rounded-lg shadow-lg"
                            onClick={
                                roomData?.isAdmin && roomData?.readyToStart
                                    ? createGame
                                    : toggleReadyStatus
                            }
                        >
                            {roomData?.isAdmin && roomData?.readyToStart
                                ? "Begin Game"
                                : roomData?.isReady
                                ? "Not Ready"
                                : "Ready"}
                        </button>
                    </div>
                </div>
            )}
        </MainLayout>
    );
}
