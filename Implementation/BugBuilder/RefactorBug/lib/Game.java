
public class Game {
	DoubleCycleLinkList list=new DoubleCycleLinkList();
	int num;
	int key;
	
	public Game(int num,int key)
	{
		this.num=num;
		this.key=key;
	}
	public void play() throws Exception
	{
		for(int i=0;i<num;i++)
		{
			list.insert(i, i);
		}
		System.out.println("-------游戏开始前------");
		for(int i=0;i<list.size;i++)
		{
			System.out.print(list.get(i)+" ");
		}
		System.out.println("-------游戏开始-----");
		int iCount=num;
		int j=0;
		Node node=list.head;
		while(iCount!=1)
		{
			if(node.getElement()!=null&&Integer.parseInt(node.getElement().toString())!=-1)
			{
				j++;
				if(j%key==0)
				{
					node.setElement(-1);
					iCount--;
					System.out.println();
					for(int i=0;i<list.size;i++)
					{
						System.out.print(list.get(i)+" ");
					}
				}
			}
			node=node.next;
		}
		System.out.println("----游戏结束------");
		for(int i=0;i<list.size;i++)
		{
			System.out.print(list.get(i)+" ");
		}
	}

}
